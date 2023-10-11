package data;

import domain.OVChipkaart;
import domain.Product;

import java.util.List;

public interface ProductDAO {
    public List<Product> findAll();
    public boolean updateLink(Product product);
    public boolean save(Product product);
    public boolean update(Product product);
    public boolean delete(Product product);
    public boolean unlinkProductOV(int ovChipkaart, Product product);
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart);
    public boolean linkProductOV(int ovChipkaart, Product product);


}
