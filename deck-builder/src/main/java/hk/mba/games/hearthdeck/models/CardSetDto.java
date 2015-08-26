package hk.mba.games.hearthdeck.models;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface CardSetDto extends JSObject {

	@JSProperty
	public String getName();

	@JSProperty
	public int getSize();
}
