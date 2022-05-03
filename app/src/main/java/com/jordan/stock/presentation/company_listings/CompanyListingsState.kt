package com.jordan.stock.presentation.company_listings

import com.jordan.stock.domain.model.CompanyListing

data class CompanyListingsState(
    val companies: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefresh: Boolean = false,
    val searchQuery: String = ""
)
