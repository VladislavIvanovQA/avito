package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceFormatted{
    public Title title;
    public String titleDative;
    public String postfix;
    public boolean enabled;
    public String postfix_short;
    public boolean was_lowered;
    public boolean has_value;
    public String string;
    public Integer value;
    public Integer old_value;
    public String old_string;
    public String exponent;
}
