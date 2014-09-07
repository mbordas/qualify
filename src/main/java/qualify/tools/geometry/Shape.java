package qualify.tools.geometry;

import org.jdom.Element;

public abstract class Shape {
	
	String color = null;
	String label = null;
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setColor(String color) {
		this.color = color;
	}

	public abstract Element toDomElement();

}
