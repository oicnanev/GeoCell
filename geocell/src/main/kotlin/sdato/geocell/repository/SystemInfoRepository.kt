package sdato.geocell.repository

import sdato.geocell.domain.entities.About

interface SystemInfoRepository {
    fun getAbout(): About
}