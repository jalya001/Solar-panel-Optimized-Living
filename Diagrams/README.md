# /////Solcellepaneller App 

## 1. Introduksjon

  Solcellepaneller App er en Android-applikasjon som hjelper brukere med å estimere solpanelers effektivitet og potensielle kostnadsbesparelser basert på klimadata.
  Appen bruker eksterne API-er for å hente værdata og solenergiinformasjon for spesifikke lokasjoner. Brukeren kan legge inn detaljer om taket sitt,
  hente gjennomsnittlig klimadata, og få estimater for solenergiutnyttelse.
### 2. Funksjoner

    Kartvisning: Brukeren kan legge inn en adresse som konverteres til GPS-koordinater, og plassere en markør på kartet.

    Kalkulering av solenergi: Basert på takdetaljer (vinkel, effektivitet) og klimadata, beregner appen forventet solenergi i kWh.

    API-integrasjon: Appen bruker eksterne API-er som Frost API for værdata og PVGIS API for solenergiestimat.

### 3. Krav for å kjøre appen

  For å kjøre appen på din lokale maskin, trenger du følgende:
   ### Krav:

    Android Studio.

    Android Emulator eller en fysisk Android-enhet.

    Minimum API-nivå 26 (Android 8.0) for å kjøre appen.

    Internettilgang for API-forespørsler.

### API:

Appen bruker flere eksterne API-er som krever API-nøkler for tilgang:

    Google Maps API for kartvisning.

    Geocoder API for koordinater.

    HvakosterStrømmenAPi for strømpris.

    Frost API for værdata.

    PVGIS API for solenergiestimat.

### 4. Kjøring av Appen
#### Steg 1: Klon prosjektet
  ```
  git clone <repo-url>
  cd <project-folder>
```

#### Steg 2: Åpne prosjektet i Android Studio

    Velg "Open" i Android Studio og pek på mappen der prosjektet er lagret.

#### Steg 3: Synkroniser avhengigheter

    Når prosjektet er åpnet, vil Android Studio automatisk prøve å laste ned alle nødvendige avhengigheter via Gradle. Hvis dette ikke skjer automatisk, klikk på Sync Now.

#### Steg 4: Kjør appen

    Velg en Android-emulator eller fysisk enhet, og klikk på "Run" i Android Studio for å kjøre appen.
### 5. Libraries og APier

Appen bruker flere biblioteker for å håndtere ulike oppgaver som UI-komponenter, API-kommunikasjon og datalagring. Her er en liste over de viktigste avhengighetene:
#### 1. Jetpack Compose

Jetpack Compose er et deklarativt UI-rammeverk som gjør det enklere å bygge brukergrensesnitt i Android. Det lar utviklere definere UI-komponenter i Kotlin uten å bruke XML-layouts.

    Bruksområde: Hele brukergrensesnittet i appen er bygget med Jetpack Compose.

#### 2. KTOR

KTOR er et Kotlin-basert bibliotek for HTTP-baserte applikasjoner. Det brukes for å lage HTTP-forespørsler til eksterne API-er, som Frost API for værdata.

    Bruksområde: Håndtering av nettverksforespørsler til eksterne API-er.

#### 3. ViewModel & LiveData

ViewModel og LiveData fra Android Jetpack gir en arkitekturell tilnærming til å håndtere UI-relatert data. ViewModel holder på appens data og sikrer at den ikke går tapt ved endringer i UI (f.eks. skjermrotasjon). LiveData brukes til å observere data og automatisk oppdatere UI når data endres.

    Bruksområde: Brukes til tilstandshåndtering i appens ViewModel-lag for å sikre at UI er i synk med dataene.

#### 4. Maps SDK

Google Maps SDK for Android gir muligheten til å integrere Google Maps i Android-applikasjoner, som muliggjør visning av kart, geokoding (adresse til koordinater) og plassering av markører.

    Bruksområde: Vist på MapScreen, hvor brukeren kan søke etter adresser, vise kart og plassere markører.

#### 5. Geocoder API

Geocoder API brukes til å hente geografiske koordinater (breddegrad og lengdegrad) basert på en adresse, og omvendt.

    Bruksområde: Brukes i MapScreen for å konvertere brukerens adresse til koordinater.
#### 5. HvaKosterStrømmen API

API brukes til å hente strømpris for førkjellige områder.

    Bruksområde: Brukes i PriceScreen, og viser pris og brukes for å lage grafer med prisene.
### 7. Forbedringsmuligheter

Dette prosjektet har potensiale til å implementere flere funksjoner som:

    Offline lagring av brukerdata for bedre tilgjengelighet.

    Flere API-integrasjoner for mer detaljert solenergi-estimat.

    Analyser for å gi brukeren anbefalinger om takvinkel og effektivitetsinnstillinger for optimal solenergiutnyttelse

    Lagring av Posisjoner 
    
