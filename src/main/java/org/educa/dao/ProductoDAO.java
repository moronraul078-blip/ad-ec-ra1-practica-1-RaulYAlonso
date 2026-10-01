package org.educa.dao;

import generated.Producto;
import jakarta.xml.bind.JAXBException;
import org.educa.entity.ProductoEntity;

import java.io.File;
import java.util.List;

/**
 * Interface defines the Data Access Operations for the products stored in the XML
 * Uses JAXB
 */
public interface ProductoDAO {
    /**
     * Reads the XML and deserializes its content in java objects {@link Producto}
     *
     * @param xmlFile XML File with the products list
     * @return Java list of {@link Producto} that JAXB transformed from the XML
     * @throws JAXBException If there is a deserializing error JAXB
     */

    List<Producto> transformXML(File xmlFile) throws JAXBException;
}
