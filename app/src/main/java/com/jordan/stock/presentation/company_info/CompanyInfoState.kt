package com.jordan.stock.presentation.company_info

import com.jordan.stock.domain.model.CompanyInfo
import com.jordan.stock.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
