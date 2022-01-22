package tcc.poc.oauth;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import tcc.poc.model.User;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DynamoClient implements ClientDetailsService, ClientRegistrationService {

    @Inject
    private AmazonDynamoDB amazonDynamoDB;

    private DynamoDB dynamoDB;
    private String tableNameClient = "TABLE_CLIENT";
    private String tableNameUser = "TABLE_USER";

    @PostConstruct
    public void createDynamoDB() {
        this.dynamoDB = new DynamoDB(amazonDynamoDB);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Table table = dynamoDB.getTable(tableNameUser);
        Item item = table.getItem("clientId", clientId);
        ClientDetails clientDetails = this.getClientDetailsFromItem(item);
        return clientDetails;
    }

    public Boolean isValidUsername(String username, String password) throws ClientRegistrationException {
        Table table = dynamoDB.getTable(tableNameClient);
        Item item = table.getItem("username", username);
        User user = this.getUserFromItem(item);
        return user.getUsername()!=null && (user.getPassword()!=null && user.getPassword().equals(password));
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        Table table = dynamoDB.getTable(tableNameUser);
        List<ClientDetails> allClientDetails = new ArrayList<>();
        table.scan(new ScanSpec()).forEach(item -> allClientDetails.add(getClientDetailsFromItem(item)));
        return allClientDetails;
    }

    private ClientDetails getClientDetailsFromItem(Item item) {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId(item.getString("clientId"));
        clientDetails.setClientSecret(getClientSecret(item.getString("clientSecret")));

        List<String> scopes = item.getList("scopes");
        clientDetails.setScope(scopes.stream().distinct().collect(Collectors.toList()));
        List<String> grantTypes = item.getList("authorizedGrantTypes");
        clientDetails.setAuthorizedGrantTypes(grantTypes.stream().distinct().collect(Collectors.toList()));
        return clientDetails;
    }

    private User getUserFromItem(Item item) {
        User user = new User();
        user.setUsername(item.getString("username"));
        user.setPassword(item.getString("password"));
        return user;
    }

    public String getClientSecret(String clientSecret) {
        return "{noop}"+clientSecret;
    }



    //Não necessário implementação para demonstração
    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {

    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {

    }

    @Override
    public void updateClientSecret(String s, String s1) throws NoSuchClientException {

    }

    @Override
    public void removeClientDetails(String s) throws NoSuchClientException {

    }

    /*
    private Item getItemFromClientDetails(ClientDetails clientDetails) {
        return new Item().withPrimaryKey("clientId", clientDetails.getClientId())
                .withString("clientSecret", clientDetails.getClientSecret())
                .withStringSet("scopes", clientDetails.getScope())
                .withStringSet("authorizedGrantTypes", clientDetails.getAuthorizedGrantTypes());
    }
     */
}
