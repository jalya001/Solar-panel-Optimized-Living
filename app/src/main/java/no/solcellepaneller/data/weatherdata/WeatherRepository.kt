package no.solcellepaneller.data.weatherdata

import no.solcellepaneller.model.weather.Energy
import android.util.Log

class WeatherRepository(private val dataSource: PVGISDataSource){
    suspend fun getSolarEnergyInfo(): List<Energy>{
        val energyList = dataSource.getSolarEnergy()
        Log.d("WeatherRepository", "Energy data received: $energyList")
        return energyList
    }
}