package org.educa.service;

import generated.Producto;
import jakarta.xml.bind.JAXBException;
import org.educa.dao.ProductoDAO;
import org.educa.dao.ProductoDAOImpl;
import org.educa.dao.SummaryDAO;
import org.educa.dao.SummaryDAOImpl;
import org.educa.entity.ProductoEntity;
import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for the products inventory.
 */
public class ProductoService {
    ProductoDAO productoDAO = new ProductoDAOImpl();
    SummaryDAO summaryDAO = new SummaryDAOImpl();

    /**
     * Method that reads a List<Producto> to make a List<ProductoEntity>
     *
     * @param fileXml path to the XML file with the products
     * @return list of {@link ProductoEntity} with the calculated values
     * @throws JAXBException JAXB exception
     */
    public List<ProductoEntity> readFile(String fileXml) throws JAXBException {
        // Create the List of ProductoEntity that we will return
        List<ProductoEntity> productos = new ArrayList<>();
        //create a file in base the String which is the route
        File file = new File(fileXml);
        //make a List<Producto> to save the iterations
        List<Producto> products = productoDAO.transformXML(file);

        // Loop to cast Productos and make productoEntities
        for(Producto p : products){
            productos.add(castToProductoEntity(p));
        }

        //Returns the cast list
        return productos;
    }

    /**
     * Exports the summary of the products read from the XML into a File object with plain text
     *
     * @param path target directory path where the summary file is saved
     * @param fileXml path to source XML file with the products for the {@link readFile}
     * @throws JAXBException parsing exception
     * @throws IOException input or output exception when writing the file
     */
    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        List<ProductoEntity> productos = readFile(fileXml);
        File originalFile = new File(fileXml);

        SummaryEntity summary = builderSummaryEntity(productos, originalFile);

        String resultFileName = "result_" + summary.getName() + ".txt";
        File exportDir = new File(path);
        File resultFile = new File(exportDir, resultFileName);

        summaryDAO.exportSummary(summary, resultFile);
    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }

    /**
     * Casts the raw data from a {@link Producto} to a {@link ProductoEntity}
     * applying all the math operations needed
     *
     * @param p Producto instance deserialized from the XML by the DAO
     * @return ProductoEntity with the math operations done
     */
    private ProductoEntity castToProductoEntity (Producto p) {
        ProductoEntity pe = new ProductoEntity();
        pe.setProducto(p);

        /*
            Discounts:
            As we are working with BigDecimal the operators we know doesn't work
            so we need to use different ones: .add = +, .subtract = -, .multiply = * and .divide = /
        */
        BigDecimal discount = (p.getPrecio().multiply(p.getDescuento())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        pe.setPrecioFinal(p.getPrecio().subtract(discount));

        // Total costs
        pe.setCost(p.getCostes().getCostesEnvio().add(p.getCostes().getCostesAlmacenaje()));
        pe.setProfit(pe.getPrecioFinal().subtract(pe.getCost()));

        return pe;
    }

    /**
     * Builds a {@link SummaryEntity} setting all the calculated data
     *
     * @param productos list of {@link ProductoEntity}
     * @param originalFile source XML {@link File} to get the data
     * @return a {@link SummaryEntity} instance ready with all the data to export
     */
    private SummaryEntity builderSummaryEntity (List<ProductoEntity> productos, File originalFile) {
        String dateSuffix = builderDateSuffix(originalFile.getName());
        BigDecimal totalProfit = calculateTotalProfit(productos);

        SummaryEntity summary = new SummaryEntity();
        summary.setName(dateSuffix);
        summary.setNumberOfProducts(productos.size());
        summary.setTotalProfit(totalProfit);
        summary.setFileAbsolutePath(originalFile.getAbsolutePath());
        summary.setFileName(originalFile.getName());
        summary.setFileSize(originalFile.length());

        return summary;
    }

    /**
     * Iterates the list of {@link ProductoEntity} to calculate the total profit
     *
     * @param productos list of {@link ProductoEntity} with all the profits
     * @return calculated sum of all the profits as a {@link BigDecimal}
     */
    private BigDecimal calculateTotalProfit (List<ProductoEntity> productos) {
        BigDecimal totalProfit = BigDecimal.ZERO;
        for (ProductoEntity pe : productos) {
            if (pe.getProfit() != null) {
                totalProfit = totalProfit.add(pe.getProfit());
            }
        }
        return totalProfit;
    }

    /**
     * Extracts ONLY the date suffix from the XML file name
     *
     * @param fileName is the raw file name (e.g. inventario_junio2026.xml)
     * @return date String (e.g. junio2026)
     */
    private String builderDateSuffix (String fileName) {
        return fileName.replace("inventario_", "").replace(".xml","");
    }
}
