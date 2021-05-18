package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ext{
    public String bezopasnyi_prosmotr;
    public String address;
    public String deposit;
    public String commission;
    public String offer_type;
    public String house_area;
    public String site_area;
    public String distance_to_city;
    public String lease_period;
    public String type;
    public String material_sten;
    public String floors_count;
    public Title title;
    @JsonIgnore
    public List<Dopolnitelno> dopolnitelno;
    @JsonIgnore
    public String kolichestvo_spalnykh_mest;
    @JsonIgnore
    public String kolichestvo_krovatey;
    @JsonIgnore
    public String multimedia;
    @JsonIgnore
    public String bytovaya_tekhnika;
    @JsonIgnore
    public String komfort;
    public String commission_amount;
}
