INNHOLD
    Beskrivelse og diagrammer, vi anbefaler å generere dem med
    Mermaid som vist på forelesning. Se kravene til modellering
    lenger ned i dette dokumentet. Ha med hvorfor diagrammet er
    valgt og hva dere ønsker å med det.
MODELING
Class Diagram 1st draft
```mermaid

    classDiagram
    class HomeScreen{
        +Composabele DisplayElectricityPrice(HomeViewModel)
        +Composable DispalyInfo()
    }
     class MapScreen{
        +Composable DisplayAdressFelt()
        +Composable DispalyMap(MapViewModel)
        +Composable SendAdressButton(MapViewModel,String Adress)
    }
    class ResultScreen{
        +Composable DisplayInfo()
        +Composable DispalyResults(WeatherDataViewModel)
        +Composable Diplay MonthlySavings(WeatherDataViewModel)

        
    }
    %% ViewModels
   class HomeViewModel {
        +Double getElectricityPriceForToday(ElectricityPriceRepository)
        
    }

    class MapViewModel {
        +Pair<Double, Double> getLatLong(AddressRepository, String Adress)
        +Map getMapData(MapRepository)
    }

    class WeatherDataViewModel {

        +List<AvergeMonthlySnowCover> monthlySnowData(SnowCoverRepository)
        +List<AvergeMonthlyTemperatures> monthlyTempData(TemperatureRepository)
        +List<AvergeMonthlyClouds> monthlyCloudData(CloudCoverRepository)

        +List<Radiation> monthlyRadiation(SolarEnergyRepository)
        +List<EnergyProduction> monthlyEnergy(SolarEnergyRepository)
        +List<MonthlySavings> monthlySavings(SolarEnergyRepository)
        
        fetchWeatherData(latLong: Pair<Double, Double>,TemperatureRepository,CloudCoverRepository,SnowCoverRepository,SolarEnergyRepository)

        
    }

    %% Repositories
    class ElectricityPriceRepository {
        +Double fetchElectricityPriceForToday(HvaErStrømprisenApiDataSource)
        +List<MonthlyPrice> fetchMonthlyPrice(HvaErStrømprisenApiDataSource)
    }

    class AddressRepository {
        +Pair<Double, Double> fetchCurrentAddress(GeocoderDataSource,String Adress)
    }

    class MapRepository {
        +Map fetchMapData(MapDataSource)
        
    }

    class TemperatureRepository {
         +List<Temperatues> fetchTemp(FrostApiDataSource, latLong: Pair<Double, Double>)

        +List<AvergeMonthlyTemperatures> calculateAverge(fetchTemp)
    }
    

    class SnowCoverRepository {

        +List<Snows> fetchSnowCover(FrostApiDataSource,latLong: Pair<Double, Double>)

        +List<AvergeMonthlySnowCover> calculateAverge(fetchSnowCover)
    }

    class CloudCoverRepository {
        +List<Clouds> fetchCloudCover(FrostApiDataSource,latLong: Pair<Double, Double>)

        +List<AvergeMonthlyClouds> calculateAverge(fetchCloudCover)
    }

    class SolarEnergyRepository {
         +List<Radiation> fetchSolarRadiation(PVGISApiDataSource,latLong: Pair<Double, Double>)

        +List<EnergyProduction> fetchEnergyProduced(PVGISApiDataSource,latLong: Pair<Double, Double>)

        +List<MonthlySavings> FetchSavings(List<EnergyProduction>,ElectricityPriceRepository )
    }

    %% Data Sources / APIs
    class HvaErStrømprisenApiDataSource {
        +Double getElectricityPriceQuery()
        +List<monthlyPrice> getMonthlyPriceQuery()
    }

    class GeocoderDataSource {
        +LiveData<List<Pair<Double, Double>>>getAddressQuery(String Adress)
    }

    class MapDataSource {
        +Map fetchMapDataQuery()
    }

    class FrostApiDataSource {
        +List<Temperature> getTemperatureQuery(latLong: Pair<Double, Double>)
        +List<Snow> getSnowCoverQuery(latLong: Pair<Double, Double>)
        +List<Cloud> getCloudCoverQuery(latLong: Pair<Double, Double>)
    }

    class PVGISApiDataSource {
        +List<Radation> getSolarRadiationQuery(latLong: Pair<Double, Double>)
        +List<EnergyProduction> getEnergyProducedQuery(latLong: Pair<Double, Double>)
    }
    %% Data classes
    class Temperature{
        date 
        value

    }
    class Snow{
     date 
        value
    }
    class Cloud{
     date 
        value
    }
    class Energy{
     date 
        value
    }
    class Radiation{
     date 
        value
    }
    class Price{
     date 
        value
    }
    
    %% Relationships
    HomeScreen --> HomeViewModel
    ResultScreen --> WeatherDataViewModel
    MapScreen --> MapViewModel
    HomeViewModel --> ElectricityPriceRepository
    
    ElectricityPriceRepository --> HvaErStrømprisenApiDataSource
    HvaErStrømprisenApiDataSource --> Price

    MapViewModel --> AddressRepository
    MapViewModel --> MapRepository
    AddressRepository --> GeocoderDataSource
    MapRepository --> MapDataSource
    
    WeatherDataViewModel --> MapViewModel
    WeatherDataViewModel --> TemperatureRepository
    WeatherDataViewModel --> SnowCoverRepository
    WeatherDataViewModel --> CloudCoverRepository
    WeatherDataViewModel --> SolarEnergyRepository

    TemperatureRepository --> FrostApiDataSource
    SnowCoverRepository --> FrostApiDataSource
    CloudCoverRepository --> FrostApiDataSource

    SolarEnergyRepository --> PVGISApiDataSource

    FrostApiDataSource --> Cloud
    FrostApiDataSource --> Temperature
    FrostApiDataSource --> Snow

    PVGISApiDataSource --> Radiation
    PVGISApiDataSource --> Energy
```




Class Diagram later draft
```mermaid
    classDiagram
    class HomeScreen{
        +Composabele DisplayElectricityPrice(HomeViewModel)
        +Composable DispalyInfo()
    }
     class MapScreen{
        +Composable DisplayAdressFelt()
        +Composable DispalyMap(MapViewModel)
        +Composable SendAdressButton(MapViewModel,String Adress)
    }
    class ResultScreen{
        +Composable DisplayInfo()
        +Composable DispalyResults(WeatherDataViewModel)
        +Composable Diplay MonthlySavings(WeatherDataViewModel)
    }
    class PriceScreen{
        +Composabele DisplayElectricityPrice(HomeViewModel)
    }
    %% ViewModels
   class HomeViewModel {
        +Double getElectricityPriceForToday(ElectricityPriceRepository)
        
    }
    class PriceViewModel {

    }

    class MapViewModel {
        +Pair<Double, Double> getLatLong(AddressRepository, String Adress)
        +Map getMapData(MapRepository)
    }

    class WeatherDataViewModel {

        +List<AvergeMonthlySnowCover> monthlySnowData(SnowCoverRepository)
        +List<AvergeMonthlyTemperatures> monthlyTempData(TemperatureRepository)
        +List<AvergeMonthlyClouds> monthlyCloudData(CloudCoverRepository)

        +List<Radiation> monthlyRadiation(SolarEnergyRepository)
        +List<EnergyProduction> monthlyEnergy(SolarEnergyRepository)
        +List<MonthlySavings> monthlySavings(SolarEnergyRepository)
        
        fetchWeatherData(latLong: Pair<Double, Double>,TemperatureRepository,CloudCoverRepository,SnowCoverRepository,SolarEnergyRepository)

        
    }

    %% Repositories
    class ElectricityPriceRepository {
        +Double fetchElectricityPriceForToday(HvaErStrømprisenApiDataSource)
        +List<MonthlyPrice> fetchMonthlyPrice(HvaErStrømprisenApiDataSource)
    }

    class AddressRepository {
        +Pair<Double, Double> fetchCurrentAddress(GeocoderDataSource,String Address)
        +Pair<Double, Double> fetchCurrentAddress(ElevationApi,String Address)
    }

    class WeatherRepository {
        +List<> fetchWeatherData(FrostApiDataSource, latLong: Pair<Double, Double>)
        +List<> fetchRimData(FrostApiDataSource, latLong: Pair<Double, Double>)

        +List<AvergeMonthlyTemperatures> calculateAverge(fetchTemp)
        +List<AvergeMonthlySnowCover> calculateAverge(fetchSnowCover)
        +List<AvergeMonthlyClouds> calculateAverge(fetchCloudCover)
        
        +List<Radiation> fetchSolarRadiation(PVGISApiDataSource,latLong: Pair<Double, Double>)

        +List<EnergyProduction> fetchEnergyProduced(PVGISApiDataSource,latLong: Pair<Double, Double>)

        +List<MonthlySavings> FetchSavings(List<EnergyProduction>,ElectricityPriceRepository )
   
    }


    %% Data Sources / APIs
    class HvaErStrømprisenApiDataSource {
        +Double getElectricityPriceQuery()
        +List<monthlyPrice> getMonthlyPriceQuery()
    }

    class GeocoderDataSource {
        +LiveData<List<Pair<Double, Double>>>getAddressQuery(String Adress)
    }

    class ElevationApi {
        +Map fetchElevation()
    }

    class FrostApiDataSource {
        +List<Temperature> getTemperatureQuery(latLong: Pair<Double, Double>)
        +List<Snow> getSnowCoverQuery(latLong: Pair<Double, Double>)
        +List<Cloud> getCloudCoverQuery(latLong: Pair<Double, Double>)
    }

    class PVGISApiDataSource {
        +List<Radation> getSolarRadiationQuery(latLong: Pair<Double, Double>)
        +List<EnergyProduction> getEnergyProducedQuery(latLong: Pair<Double, Double>)
    }
    %% Data classes
    class Temperature{
        date 
        value

    }
    class Snow{
     date 
        value
    }
    class Cloud{
     date 
        value
    }
    class Energy{
     date 
        value
    }
    class Radiation{
     date 
        value
    }
    class Price{
     date 
        value
    }
    
    %% Relationships
    
    HomeScreen --> HomeViewModel
    ResultScreen --> WeatherDataViewModel
    MapScreen --> MapViewModel
    HomeViewModel --> ElectricityPriceRepository
    HomeViewModel --> WeatherRepository

    PriceScreen --> PriceViewModel
    PriceViewModel --> ElectricityPriceRepository
    ElectricityPriceRepository --> HvaErStrømprisenApiDataSource
    HvaErStrømprisenApiDataSource --> Price

    MapViewModel --> AddressRepository
    AddressRepository --> GeocoderDataSource
    AddressRepository --> ElevationApi
    
    WeatherDataViewModel --> AddressRepository
    WeatherDataViewModel --> WeatherRepository

    WeatherRepository --> FrostApiDataSource

    WeatherRepository --> PVGISApiDataSource

    FrostApiDataSource --> Cloud
    FrostApiDataSource --> Temperature
    FrostApiDataSource --> Snow

    PVGISApiDataSource --> Radiation
    PVGISApiDataSource --> Energy

    

MapScreen: Find Adress
```mermaid
    sequenceDiagram

    actor User
    participant HomeScreen
    participant MapScreen
    participant MapViewModel
    participant AddressRepository
    participant AddressDataSource

    User ->> HomeScreen: Click MapScreen button
    HomeScreen ->> MapScreen: Open MapScreen
    MapScreen ->> User: DiplayMap 

    User ->> MapScreen : Input Adress
    alt sucsess
        MapScreen ->> MapViewModel: Request Location Data
        MapViewModel ->> AddressRepository: Fetch Adress Coordinates
        AddressRepository ->> AddressDataSource: Call Api
        AddressDataSource -->> AddressRepository: Return Reponse
        AddressRepository -->> MapViewModel: Return Processed Coordinates
        MapViewModel -->> MapScreen : Send Adress Coordinates
        
    else fail 
        MapScreen ->> MapViewModel: Request Location Data
        MapViewModel ->> AddressRepository: Fetch Adress Coordinates
        AddressRepository ->> AddressDataSource: Call Api
        AddressDataSource -->> AddressRepository: Return Error
        AddressRepository -->> MapViewModel: Return Error
        MapViewModel -->> MapScreen : Return Error Message
        MapScreen ->> User: Display Error Message

    end
```
    
    
MapScreen: DrawArea

```mermaid
    sequenceDiagram

    actor User
    
    participant MapScreen
    participant MapViewModel
    
    participant Nav
    participant ResultScreen

    User ->> MapScreen: Click on map to add a point
    MapScreen ->> MapViewModel: Add point to polygon data
    MapViewModel ->> MapScreen: Update polygon points

    User ->> MapScreen: Click "Show AREA"
    MapScreen ->> MapViewModel: Calculate polygon area
    MapViewModel ->> MapScreen: Return calculated area
    MapScreen ->> User: Display area and prompt for additional data

    User ->> MapScreen: Input slope and efficiency
    MapScreen ->> MapViewModel: Store slope and efficiency values
    MapViewModel ->> MapScreen: Update UI with values

    User ->> MapScreen: Click "Remove Last Point"
    MapScreen ->> MapViewModel: Remove last point from polygon
    MapViewModel ->> MapScreen: Update polygon points

    User ->> MapScreen: Click "Remove Points"
    MapScreen ->> MapViewModel: Clear all polygon points
    MapViewModel ->> MapScreen: Update UI

    alt if polygon is visble 

    User ->> MapScreen : Click "Go to Results"
    MapScreen ->> Nav : Show Result Screen
    Nav ->> ResultScreen : Display Screen
    ResultScreen ->>User : Display Results

    else if polygon is not visible
    User ->> MapScreen : Click "Go to Results"
    MapScreen ->> User : Show Error Message
    end

```
PriceScreen: fetching prices and display
```mermaid
sequenceDiagram
actor User

participant PriceScreen
participant PriceScreenViewModel
participant RegionDropdown
participant Repository
participant Nav
participant HelpBottomSheet
participant AppearanceBottomSheet

User ->> PriceScreen: Screen opened
PriceScreen ->> RequestLocationPermission: Request location
RequestLocationPermission ->> PriceScreen: Return region
PriceScreen ->> PriceScreenViewModel: setRegion(region)
PriceScreenViewModel ->> Repository: updateRegion(region)

User ->> RegionDropdown: Select a different region
RegionDropdown ->> PriceScreen: onRegionSelected(region)
PriceScreen ->> PriceScreenViewModel: setRegion(region)
PriceScreenViewModel ->> Repository: updateRegion(region)

User ->> PriceScreen: Wait for data to load
PriceScreenViewModel ->> Repository: updatePrices(date)
Repository ->> PriceScreenViewModel: Return price list
alt prices are returned
    PriceScreenViewModel ->> PriceScreen: Emit Success(prices)
    PriceScreen ->> PriceScreen: Display chart and PriceCard
else no prices found
    PriceScreenViewModel ->> PriceScreen: Emit Error
    PriceScreen ->> PriceScreen: Show ErrorScreen
end

User ->> BottomBar: Click Help
BottomBar ->> HelpBottomSheet: Show help

User ->> BottomBar: Click Appearance
BottomBar ->> AppearanceBottomSheet: Show appearance settings


```
![Alt text](UseCaeT37.svg)
