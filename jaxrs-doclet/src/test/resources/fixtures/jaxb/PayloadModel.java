package fixtures.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "payload")
public class PayloadModel {

    public String getVisibleField() {
        return "";
    }

    @XmlElement(name = "odd-name")
    public String oddlyNamedField() {
        return "";
    }

    @XmlTransient
    public String getInvisibleField() {
        return "";
    }

}
