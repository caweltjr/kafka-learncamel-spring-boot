package com.learncamel.processor;

import com.learncamel.domain.Item;
import com.learncamel.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class BuildSQLProcessor implements org.apache.camel.Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String tableName = "ITEMS";
        Item item = (Item) exchange.getIn().getBody();
        log.info("Item in Processor is " + item);
        if(ObjectUtils.isEmpty(item.getSku())){
            throw new DataException("Sku is null for " + item.getItemDescription());
        }
        StringBuilder query = new StringBuilder();
        if(item.getTransactionType().equals("ADD")){
            query.append("INSERT INTO ITEMS(SKU,ITEM_DESC,ITEM_PRICE) VALUES ('" + item.getSku()
            + "','" + item.getItemDescription() + "'," + item.getPrice() + ")");
        }else if(item.getTransactionType().equals("UPDATE")) {
            query.append("UPDATE " + tableName + " SET ITEM_PRICE = " + item.getPrice() +
                    " WHERE SKU = '" + item.getSku() + "'");
        }else if(item.getTransactionType().equals("DELETE")){
            query.append("DELETE FROM " + tableName + " WHERE SKU = '" + item.getSku() + "'");
        }
        log.info("Final Query is " + query.toString());
        exchange.getIn().setBody(query.toString());
        exchange.getIn().setHeader("skuid", item.getSku());
    }
}
