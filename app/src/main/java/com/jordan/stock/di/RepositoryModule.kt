package com.jordan.stock.di

import com.jordan.stock.data.csv.CSVParser
import com.jordan.stock.data.csv.CompanyListingsParser
import com.jordan.stock.data.csv.IntradayInfoParser
import com.jordan.stock.data.repository.StockRepositoryImpl
import com.jordan.stock.domain.model.CompanyListing
import com.jordan.stock.domain.model.IntradayInfo
import com.jordan.stock.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>
}