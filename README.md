# AutoDripstone Mod

Automatyczne tworzenie trapów z dripstone za pomocą klapy (trapdooru).

## Jak to działa

1. Postaw **klawę (trapdoor)** w miejscu, gdzie chcesz trap
2. **Miej dripstone (Pointed Dripstone) w hotbarze**
3. **Spójrz na klawę** 
4. **Trzymaj prawy przycisk myszy** (atak)
5. Mod automatycznie:
   - **Bierze dripstone z hotbaru gracza**
   - Stawia go **POD klawą** (zwisający)
   - Otwiera klawę = dripstone pada na gracza poniżej
   - Zamyka klawę = bierze następny dripstone z hotbaru
   - Powtarza to w pętli

6. Puść przycisk = stop

## Konfiguracja

Plik: `.minecraft/config/autodripstone.json`

```json
{
  "trapdoorToggleSpeed": 2,
  "dripstoneSpawnChance": 1,
  "enabled": true
}
```

### Parametry:
- **trapdoorToggleSpeed** (1-20): Prędkość otwierania/zamykania (niska = szybciej, 1 = max)
- **dripstoneSpawnChance** (1-100): Szansa spawnięcia dripstone
- **enabled**: Włącz/wyłącz mod

## Wymagania

- **Minecraft**: 1.21.4
- **Fabric Loader**: 0.16.9+
- **Java**: 21+

## Instalacja

1. Pobierz JAR z releases
2. Umieść w `.minecraft/mods`
3. Uruchom Minecraft z Fabric

## Budowanie

```bash
./gradlew build
```

JAR będzie w `build/libs/autodripstone-1.0.0.jar`

## Licencja

MIT License - patrz LICENSE
