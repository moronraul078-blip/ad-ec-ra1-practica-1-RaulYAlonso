package org.educa.service;

import generated.Producto;
import jakarta.xml.bind.JAXBException;
import org.educa.dao.ProductoDAO;
import org.educa.dao.ProductoDAOImpl;
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

    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        List<ProductoEntity> productos = readFile(fileXml);

        File originalFile = new File(fileXml);
        String originalFileName = originalFile.getName();

        String dateSuffix = originalFileName.replace("inventario_", "")
                .replace(".xml","");

        BigDecimal totalProfit = BigDecimal.ZERO;
        for (ProductoEntity pe : productos) {
            if (pe.getProfit() != null) {
                totalProfit = totalProfit.add(pe.getProfit());
            }
        }

        SummaryEntity summary = new SummaryEntity();
        summary.setName(dateSuffix);
        summary.setNumberOfProducts(productos.size());
        summary.setTotalProfit(totalProfit);

        summary.setFileAbsolutePath(originalFile.getAbsolutePath());
        summary.setFileName(originalFileName);
        summary.setFileSize(originalFile.length());

        String resultFileName = "result_ " + dateSuffix + ".txt";
        File exportDir = new File(path);
        File resultFile = new File(exportDir, resultFileName);

        productoDAO.exportSummary(resultFile, summary);
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
}
