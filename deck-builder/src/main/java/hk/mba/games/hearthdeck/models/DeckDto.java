package hk.mba.games.hearthdeck.models;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface DeckDto extends JSObject {

	@JSProperty
	public void setId(String id);

	@JSProperty
	public String getId();

	@JSProperty
	public void setPlayerClass(String playerClass);

	@JSProperty
	public String getPlayerClass();

	@JSProperty
	public void setName(String name);

	@JSProperty
	public String getName();

	@JSProperty
	public void setCards(JSObject cards);

	@JSProperty
	public JSObject getCards();

	@JSProperty
	public void setDeckSize(int size);

	@JSProperty
	public int getDeckSize();

	@JSProperty
	public void setVarities(int varities);

	@JSProperty
	public int getVarities();

	@JSProperty
	public void setManaCost(int cost);

	@JSProperty
	public int getManaCost();

	@JSProperty
	public void setDustCost(int cost);

	@JSProperty
	public int getDustCost();

	@JSProperty
	public void setMinions(int minions);

	@JSProperty
	public int getMinions();

	@JSProperty
	public void setSpells(int spells);

	@JSProperty
	public int getSpells();

	@JSProperty
	public void setWeapons(int weapons);

	@JSProperty
	public int getWeapons();

	@JSProperty
	public void setSecrets(int secrets);

}
