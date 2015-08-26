package hk.mba.games.hearthdeck.models;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface CardDto extends JSObject {

	@JSProperty
	public String getId();

	@JSProperty
	public String getName();

	@JSProperty
	public String getType();

	@JSProperty
	public int getCost();

	@JSProperty
	public int getAttack();

	@JSProperty
	public int getHealth();

	@JSProperty
	public void setPlayerClass(String value);

	@JSProperty
	public String getPlayerClass();

	@JSProperty
	public void setSetName(String value);

	@JSProperty
	public String getSetName();

	@JSProperty
	public String getRarity();

	@JSProperty
	public String getText();

	@JSProperty
	public String getRace();

	@JSProperty
	public void setElite(boolean value);

	@JSProperty
	public boolean isElite();

	@JSProperty
	public boolean isCollectible();

	@JSProperty
	public void setDustCost(int value);

	@JSProperty
	public int getDustCost();

	@JSProperty
	public String getFullText();

	@JSProperty
	public void setFullText(String fullText);

}
