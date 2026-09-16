# ConCur — Currency Converter

## Overview of the project

ConCur is a beginner-friendly Java console application for converting amounts between currencies using locally stored exchange rates. It works offline, stores conversion history, and demonstrates the CSE2006 Programming in Java syllabus.

## Features

- Search currencies by name or code
- Convert amounts between configured currencies
- Add or update exchange rates
- Save, view, and search conversion history
- Export history to CSV
- Use JDBC/H2, with local CSV fallback
- Run a background rate-monitoring thread

The included rates are sample academic values, not live market rates.

## Technologies/tools used

- Java 8 or newer (JDK 17 recommended)
- JDBC and H2 embedded database
- Java Collections Framework and I/O streams
- `BigDecimal` for calculations
- JUnit 5 test source
- Git and GitHub

## Steps to install & run the project

### 1. Install Java

```powershell
java -version
javac -version
```

### 2. Clone the repository

```powershell
git clone https://github.com/AdIkUnDlIyA99/ConCur-Programming_in_Java-VITyarthi_Project-25BAI10634.git
cd ConCur-Programming_in_Java-VITyarthi_Project-25BAI10634
```

### 3. Compile

```powershell
New-Item -ItemType Directory -Force build\classes | Out-Null
$files = Get-ChildItem src\main\java -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d build\classes $files
```

### 4. Run

```powershell
java -cp "build\classes;lib\h2.jar" com.vit.currencyconverter.app.Main
```

The program should display `Storage mode: JDBC database`. If H2 is unavailable, run with `-cp build\classes` to use local CSV storage.

## Instructions for testing

1. Choose `1` and convert `1000 INR` to `USD`.
2. Choose `5` and confirm the conversion appears in history.
3. Choose `6`, enter `INR`, and confirm filtering works.
4. Choose `7` and confirm `exports/conversion-history.csv` is created.
5. Choose `2` and search for `Indian Rupee`.
6. Choose `8` to exit.

Invalid amounts, unknown currencies, missing rates, and invalid menu choices should produce helpful errors without terminating the program. Automated tests are under `src/test/java`.


## Project structure

```text
ConCur-Programming_in_Java-VITyarthi_Project-25BAI10634/
├── data/exchange-rates.csv       Sample exchange rates
├── lib/h2.jar                    JDBC driver included for direct running
├── pom.xml                       Optional Maven configuration
├── README.md                     This file
├── statement.md                  Project statement
└── src/
    ├── main/java/com/vit/currencyconverter/
    │   ├── app/                  Main console entry point
    │   ├── exception/            Custom validation exceptions
    │   ├── model/                Currency, rate, and history classes
    │   ├── repository/            JDBC and local-file storage classes
    │   ├── service/               Conversion and rate-management logic
    │   ├── task/                  Background rate-monitoring thread
    │   └── util/                  Database and CSV helpers
    └── test/java/com/vit/currencyconverter/
        └── service/               Conversion service tests
```
