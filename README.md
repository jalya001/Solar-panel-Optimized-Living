# SOL Solar panel Optimized Living

<div style="display: flex; gap: 10px; align-items: center;">
  <img src="https://github.uio.no/IN2000-V25/team-37/assets/11510/964b112e-85d7-4898-8f33-0fbe402b8724" alt="circular_logo_light" width="200"/>
  <img src="https://github.uio.no/IN2000-V25/team-37/assets/11510/5183e63f-e4dd-4462-8839-16379b009269" alt="circular_logo_dark" width="200"/>
</div>

### MEDLEMMER AV TEAM 37

[aazann,saleemti[https://www.linkedin.com/in/saleem-toure-issifou],mikass,anisai,jacobhl,amwarsam]
 
## 1. Introduksjon

SOL Solar panel Optimized Living er en Android-applikasjon som er utviklet for å hjelpe brukere med å estimere effektiviteten og økonomiske besparelser ved installasjon av solcellepaneler. Appen kombinerer klimadata, takspesifikasjoner og solenergi-beregninger for å gi brukeren en nøyaktig vurdering av solcelleanleggets potensial.

Ved å bruke ekstern API-integrasjon, som Frost API for værdata og andre relevante kilder for strømpriser og støtteordninger, kan brukeren få et godt grunnlag for å vurdere lønnsomheten ved solcelleinstallasjon på sin eiendom. Appen gir brukeren muligheten til å hente kartkoordinater for en spesifisert adresse, og deretter beregne forventet solenergiutnyttelse basert på takflatenes vinkel, areal, og den gjennomsnittlige solinnstrålingen i området.

Appen kan være spesielt nyttig for boligeiere, hytteeiere og profesjonelle solcelleinstallatører som ønsker å få oversikt over den potensielle strømproduksjonen og besparelsene ved å investere i solenergi. Med funksjoner som kartvisualisering, beregning av energiproduksjon og besparelser på strømregningen, samt muligheten for å tegne takflater på kartet, gir Solcellepaneller App et praktisk og brukervennlig verktøy for å optimalisere bruken av solenergi.
<div style="display: flex; gap: 10px; align-items: center;">
  <img src="https://github.uio.no/IN2000-V25/team-37/assets/11510/3b6830b5-0c26-4932-80de-82c784f265e5" alt="house" width="200"/>
  
</div>
## 2. Funksjonalieter

    Kartvisning: Brukeren kan legge inn en adresse som konverteres til GPS-koordinater, og plassere en markør på kartet.

    Kalkulering av solenergi: Basert på takdetaljer (vinkel, effektivitet) og klimadata, beregner appen forventet solenergi i kWh.

    API-integrasjon: Appen bruker eksterne API-er som Frost API for værdata og PVGIS API for solenergiestimat.

    Resultatvisning: Appen viser resulater av kalkulering med grafer
    

###  Krav for å kjøre appen

  For å kjøre appen på din lokale maskin, trenger du følgende:
   #### Krav:

    Android Studio.

    Android Emulator eller en fysisk Android-enhet.

    Minimum API-nivå 26 (Android 8.0) for å kjøre appen.

    Internettilgang for API-forespørsler.

    Enhetsposisjon brukes i appen, men ikke nødvendig.

## 3. API:

Appen bruker flere eksterne API-er som krever API-nøkler for tilgang:

    Google Maps API for kartvisning.

    Geocoder API for koordinater.

    HvakosterStrømmenAPI for strømpris.

    Frost API for værdata.

    PVGIS API for solenergiestimat.

## 4. Kjøring av Appen
#### Steg a: Klon prosjektet
  ```
  git clone <repo-url>
  cd <project-folder>
```

#### Steg b: Åpne prosjektet i Android Studio

    Velg "Open" i Android Studio og pek på mappen der prosjektet er lagret.

#### Steg c: Synkroniser avhengigheter

    Når prosjektet er åpnet, vil Android Studio automatisk prøve å laste ned alle nødvendige avhengigheter via Gradle. Hvis dette ikke skjer automatisk, klikk på Sync Now.

#### Steg d: Kjør appen

    Velg en Android-emulator eller fysisk enhet, og klikk på "Run" i Android Studio for å kjøre appen.
## 5. In depth: Libraries og APier

Appen bruker flere biblioteker for å håndtere ulike oppgaver som UI-komponenter, API-kommunikasjon og datalagring. Her er en liste over de viktigste avhengighetene:
#### a. Jetpack Compose

Jetpack Compose er et deklarativt UI-rammeverk som gjør det enklere å bygge brukergrensesnitt i Android. Det lar utviklere definere UI-komponenter i Kotlin uten å bruke XML-layouts.

    Bruksområde: Hele brukergrensesnittet i appen er bygget med Jetpack Compose.

#### b. KTOR

KTOR er et Kotlin-basert bibliotek for HTTP-baserte applikasjoner. Det brukes for å lage HTTP-forespørsler til eksterne API-er, som Frost API for værdata.

    Bruksområde: Håndtering av nettverksforespørsler til eksterne API-er.

#### c. ViewModel & LiveData

ViewModel og LiveData fra Android Jetpack gir en arkitekturell tilnærming til å håndtere UI-relatert data. ViewModel holder på appens data og sikrer at den ikke går tapt ved endringer i UI (f.eks. skjermrotasjon). LiveData brukes til å observere data og automatisk oppdatere UI når data endres.

    Bruksområde: Brukes til tilstandshåndtering i appens ViewModel-lag for å sikre at UI er i synk med dataene.

#### d. Maps SDK

Google Maps SDK for Android gir muligheten til å integrere Google Maps i Android-applikasjoner, som muliggjør visning av kart, geokoding (adresse til koordinater) og plassering av markører.

    Bruksområde: Vist på MapScreen, hvor brukeren kan søke etter adresser, vise kart og plassere markører.

#### e. Geocoder API

Geocoder API brukes til å hente geografiske koordinater (breddegrad og lengdegrad) basert på en adresse, og omvendt.

    Bruksområde: Brukes i MapScreen for å konvertere brukerens adresse til koordinater.
#### f. HvaKosterStrømmen API

API brukes til å hente strømpris for førkjellige områder.

    Bruksområde: Brukes i PriceScreen, og viser pris og brukes for å lage grafer med prisene.
## 6. Forbedringsmuligheter

Dette prosjektet har potensiale til å implementere flere funksjoner som:

    Offline lagring av brukerdata for bedre tilgjengelighet.

    Flere API-integrasjoner for mer detaljert solenergi-estimat.

    Analyser for å gi brukeren anbefalinger om takvinkel og effektivitetsinnstillinger for optimal solenergiutnyttelse

    Lagring av Posisjoner 

    Implementering av horisontal modus i appen

    
