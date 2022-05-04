package com.jordan.stock.data.repository

import com.jordan.stock.data.csv.CSVParser
import com.jordan.stock.data.local.StockDatabase
import com.jordan.stock.data.mapper.toCompanyInfo
import com.jordan.stock.data.mapper.toCompanyListing
import com.jordan.stock.data.mapper.toCompanyListingEntity
import com.jordan.stock.data.remote.StockApi
import com.jordan.stock.domain.model.CompanyInfo
import com.jordan.stock.domain.model.CompanyListing
import com.jordan.stock.domain.model.IntradayInfo
import com.jordan.stock.domain.repository.StockRepository
import com.jordan.stock.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingParser: CSVParser<CompanyListing>,
    private val IntradayInfoParser: CSVParser<IntradayInfo>,
): StockRepository{

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))

            // 初始化時先緩存全部
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() })
            )

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
            }
            emit(Resource.Loading(false))
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)
            val results = IntradayInfoParser.parse(response.byteStream())
            Resource.Success(results)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(
                "Couldn't intraday info"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                "Couldn't intraday info"
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val response = api.getCompanyInfo(symbol)
            Resource.Success(response.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(
                "Couldn't load company info"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                "Couldn't load company info"
            )
        }
    }
}