package fixtures.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("payload")
public class PayloadModel {

    public String getVisibleField() {
        return "";
    }

    @JsonProperty("odd-name")
    public String oddlyNamedField() {
        return "";
    }

    @JsonIgnore
    public String getInvisibleField() {
        return "";
    }

}
