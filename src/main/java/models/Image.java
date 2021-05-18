package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image{
    @JsonProperty("208x156")
    public String _208x156;
    @JsonProperty("416x312")
    public String _416x312;
}
