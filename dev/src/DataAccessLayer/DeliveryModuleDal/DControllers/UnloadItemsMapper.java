package DataAccessLayer.DeliveryModuleDal.DControllers;

import java.sql.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class UnloadItemsMapper extends DalController {

    private final String SiteDocIdColumnName = "SiteDocumentId";
    private final String ItemNumberColumnName = "ItemNumber";
    private final String AmountColumnName = "Amount";

    public UnloadItemsMapper() {
        super("'Unload Items'");
    }

    public Map<Integer, Integer> selectAllUnload(int siteDocId) throws Exception {
        Map<Integer, Integer> results = new HashMap<>();
        String sql = "SELECT * FROM "+getTableName()+" WHERE "+SiteDocIdColumnName+" = "+ siteDocId;
        try(Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                results.put(ConvertReaderToObject(rs), ConvertReaderToObject2(rs));
            }
            return results;
        }
        catch (Exception e) {
            throw new Exception("could not select all unload " + siteDocId);
        }
    }

    public void insert(int siteDocId, int itemNumber, int amount) throws Exception {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}) VALUES(?,?,?)",
                getTableName(), SiteDocIdColumnName, ItemNumberColumnName, AmountColumnName);
        try (Connection conn = super.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, siteDocId);
            pstmt.setInt(2, itemNumber);
            pstmt.setInt(3, amount);
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            throw new Exception("could not insert to " + getTableName());
        }
    }

    public void delete(int siteDocId, int itemNumber) throws Exception {
        if(!delete(siteDocId, itemNumber, SiteDocIdColumnName, ItemNumberColumnName))
            throw new Exception("no unload, siteDoc id:" + siteDocId + ", item number:" + itemNumber);
    }

    @Override
    protected Integer ConvertReaderToObject(ResultSet reader) throws Exception {
        int result;
        result = reader.getInt(2);
        return result;
    }

    protected Integer ConvertReaderToObject2(ResultSet reader) throws Exception {
        int result;
        result = reader.getInt(3);
        return result;
    }

    public void removeItemFromUnload(int documentId, int itemCatalogNumber, int amount) throws Exception {
        if(!delete(documentId, itemCatalogNumber, amount, SiteDocIdColumnName, ItemNumberColumnName, AmountColumnName))
            throw new Exception("could not remove item from unload");
    }

    public void AddItemToUnload(int documentId, int itemCatalogNumber, int amount) throws Exception {
        try {
            insert(documentId, itemCatalogNumber, amount);
        }
        catch (SQLException e){
            throw new Exception("could not add item to unload");
        }
    }

    public void deleteAllUnloadItemsOfSiteDoc(int siteDocid) throws Exception {
        if(!delete(siteDocid, SiteDocIdColumnName))
            throw new Exception("cant delete unload items of site doc: "+siteDocid);
    }

    @Override
    public void cleanCache() {
    }
}
