package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemsList {
    private List<Item> items;

    public void addedItems(List<Item> items) {
        if (this.items == null){
            this.items = new ArrayList<>();
        }
        this.items.addAll(items);
    }
}
