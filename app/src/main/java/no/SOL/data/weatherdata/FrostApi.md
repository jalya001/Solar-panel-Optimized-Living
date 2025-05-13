A few words on our Frost API call that did not make it into the report.

UNFINISHED


En annen bekymring var at den kunne ta for mye minne. Teoretisk verstetilfellet av obligatorisk minnebruk i API-kallet alene vil være omtrent 20mb, basert på den 3mb største forespørselen, og at det er maksimalt fem ganger algoritmen vil søke. Hvor med hvordan byene er spredd ut i Norge, hvis den treffer et tettsted, det er usannsynlig den treffer et til, som balanserer ut de større arealene på senere søk. Dette vil med OS kopier bli to til obligatoriske kopier. Koden deserialiserer (dessverre, grunnet tidsbegrensninger), og lager dermed en til kopi. Databehandling deretter, burde være ganske effektiv, med bruk av metoder som for eksempel å lagre data som sum og teller for å regne gjennomsnitt, i stedet for å lagre hvert tall separat, estimerer vi den lager gjennomsnittlig litt mindre enn en kopi av hver data. Til sammen kan vi da si at i verstetilfellet API-kall til Frost teoretisk kan ha et minnebruk på rundt 80mb, og et gjennomsnittlig minnebruk langt lavere. Siden alle moderne enheter har langt mer arbeidsminne enn den, burde ikke det være et problem. Men på en overbelastet enhet, vil alle disse minnebevegelsene kunne ta for lang tid, og dermed føre til en timeout-error. I våre tester klarte en treg emulator å få timeout-error på en 161kb forespørsel.

<<ha om hvor mange aksesseringer og sammenligninger og writes den gjør>> gjør mange unødvendige, men det er sånn høyt-nivå språk som Kotlin er, med mindre man vil gjøre det 3 ganger så vanskelig som å bare ha skrevet med C fra bunnen av. 

<<kan ikke hente bare nyere data>> <<begrunnelse for å ikke prioritere nyere data>> 