package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item{
    public String type;
    public String title;
    public boolean favorite;
    public BigInteger itemId;
    public String url;
    public List<Image> images;
    public int imagesCount;
    public boolean hasVideo;
    public Category category;
    public Ext ext;
    public int price;
    public Location location;
    public Coords coords;
    public int time;
    public PriceFormatted priceFormatted;
    @JsonIgnore
    public List<Map<Integer, String>> paramsMap;
    public Geo geo;
}
