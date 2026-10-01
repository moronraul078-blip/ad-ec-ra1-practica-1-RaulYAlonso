package org.educa.dao;

import generated.Productos;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.educa.entity.ProductoEntity;

import generated.Producto;
import java.io.File;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {
    /**
     * Implements the interface using JAXB.
     * Fir
     *
     * @param xmlFile ad-ec-ra1-practica-centenera-alonso-moron-raul\target\classes\xml
     * @return {@link List} of {@link Productos}
     * @throws JAXBException JAXB exception
     */

    @Override
    public List<Producto> transformXML(File xmlFile) throws JAXBException {
        //Creates the JAXB environment
        JAXBContext context = JAXBContext.newInstance(Productos.class);

        //The unmarshaller is the deserializer, it reads the XML
        Unmarshaller unmarshaller = context.createUnmarshaller();

        //Receives the parts from the unmarshaller and transforms them into Java objects
        Productos root = (Productos) unmarshaller.unmarshal(xmlFile);

        //Returns an internal created list of the Productos
        return root.getProducto();
    }
}
