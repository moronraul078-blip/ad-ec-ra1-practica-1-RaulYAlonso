package org.educa.service;

import generated.Producto;
import jakarta.xml.bind.JAXBException;
import org.educa.dao.ProductoDAO;
import org.educa.dao.ProductoDAOImpl;
import org.educa.entity.ProductoEntity;

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
     * method that read a List<Producto> to make a List<ProductoEntity>
     * @param fileXml path to the XML file with the products
     * @return list of {@link ProductoEntity} with the calculated values
     * @throws JAXBException JAXB exception
     */
    public List<ProductoEntity> readFile(String fileXml) throws JAXBException {
        //create the List which we will return
        List<ProductoEntity> productos = new ArrayList<>();
        //create a file in base the String which is the route
        File file = new File(fileXml);
        //make a List<Producto> to iterate it and making productoEntitys
        List<Producto> products = productoDAO.transformXML(file);
        for(Producto p : products){
            ProductoEntity pe = new ProductoEntity();
            pe.setProducto(p);
            /*
            logic of calcule the productoEntity(precioFinal), as it is a BigDecimal the operators we know doesn't work
            so we need to use .add = +, .subtract = -, .multiply = * and .divide = /
            we also save discount in another variable for performance
            */
            BigDecimal discount = (p.getPrecio().multiply(p.getDescuento())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            pe.setPrecioFinal(p.getPrecio().subtract(discount));
            pe.setCost(p.getCostes().getCostesEnvio().add(p.getCostes().getCostesAlmacenaje()));
            pe.setProfit(pe.getPrecioFinal().subtract(pe.getCost()));
            productos.add(pe);
        }
        return productos;
    }

    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        //TODO: Implementar

    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }
}
