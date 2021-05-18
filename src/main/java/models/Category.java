package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category{
    public int id;
    public int rootId;
    public String slug;
    public boolean compare;
}
