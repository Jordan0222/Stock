package com.jordan.stock.domain.repository

import com.jordan.stock.domain.model.CompanyListing
import com.jordan.stock.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}