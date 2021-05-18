package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coords{
    public double lat;
    public double lng;
    public int zoom;
    public int precision;
    public String address_user;
}
