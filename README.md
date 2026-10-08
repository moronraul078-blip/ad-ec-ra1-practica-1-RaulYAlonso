# ad-ec-ra1-practica-1-RaulYAlonso 

Our first real Java project! Made by two DAM students for the **Acceso a Datos** module (RA1 – file handling).

The idea: read product data from XML, process it, and export the results as a text summary and as Excel files. We're learning a lot along the way (and breaking a few things too ).

## What does it do?

- Reads XML data. The Java classes are generated automatically from the XSD files, so we don't parse anything by hand.
- Uses the **DAO / service / entity** structure to keep things organized: the DAOs talk to the files, the service has the logic, and the entities hold the data.
- Exports a text summary to a file (and creates the folders if they don't exist yet).
- Builds Excel files with Apache POI, with nice headers and cell styles.

## Built with

- Java 21
- Maven
- JAXB (to turn the XSD files into Java classes)
- Apache POI (for the Excel files)

## Project structure

```
.
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── org/educa
        │       ├── app        # Activity1, Activity2, Activity3 (the entry points)
        │       ├── dao        # ExcelDAO, ProductoDAO, SummaryDAO (+ their Impl classes)
        │       ├── entity     # ProductoEntity, SummaryEntity
        │       ├── service    # ProductoService
        │       └── util       # ExcelUtils
        └── resources
            ├── export         # Where the exported files end up
            ├── xml            # Input XML files
            └── xsd            # XSD files used to generate the JAXB classes
```

## How to run it

You need **JDK 21** and **Maven** installed.

```bash
git clone https://github.com/moronraul078-blip/ad-ec-ra1-practica-1-RaulYAlonso.git
cd ad-ec-ra1-practica-1-RaulYAlonso
mvn clean compile
```

The project has three entry points, one per assignment activity: `Activity1`, `Activity2` and `Activity3` (in `org.educa.app`). Open the one you want in IntelliJ and press the green play button next to its `main` method.

### Heads up about the generated classes

The JAXB classes are generated when you build the project (they end up in `target/generated-sources`), so they're **not** in the repo. If IntelliJ can't find them, just reload the Maven project, or run:

```bash
mvn clean generate-sources
```

## Exports

- **Text summary**: `SummaryDAO.exportSummary` writes the summary to a file. If the folder doesn't exist it creates it, and if the file already exists it overwrites it.
- **Excel**: `ExcelDAO` generates the spreadsheets and `ExcelUtils` has the styles we reuse (header style and data style), so all the sheets look the same.
- The exported files go to `src/main/resources/export`.

## Docs

All the methods have Javadoc.

## How we work with Git

- `main` is the stable branch, we try not to break it 
- Each task goes in its own branch (`feature/...`, `fix/...`, `docs/...`)
- Commits look like `docs(dao): add Javadoc to SummaryDAO`
- Pull request when it's ready

## Authors

- **Raúl** – [@moronraul078-blip](https://github.com/moronraul078-blip)
- **Alonso** - [@alonsocentenera](https://github.com/alonsocentenera)
