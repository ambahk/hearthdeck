package hk.mba.games.hearthdeck.models;

import hk.mba.games.tbone.TBone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.teavm.jso.JS;
import org.teavm.jso.JSArray;
import org.teavm.jso.JSObject;

public class CardDb {

	public static enum PlayerClass {
		Druid, Hunter, Mage, Paladin, Priest, Rogue, Shaman, Warrior, Warlock, Neutral
	}

	public static interface Listener {
		void execute(CardDb db);
	}

	public static interface Consumer {
		void consume(CardDto card);
	}

	private final List<Listener> listeners = new ArrayList<>();
	private CardDto[] cardList; // sorted by cost & name
	private final List<CardDto> cardListByAttack = new ArrayList<>(); // sorted, only minions
	private final List<CardDto> cardListByHealth = new ArrayList<>(); // sorted, only minions

	public void init() {
		TBone.ajaxGet("cards/AllSets.enUS.json", result -> {

			final List<CardDto> cards = new ArrayList<>();

			final JSObject sets = TBone.parseJson(result.getResponseText());
			for (final String setName : TBone.keys(sets)) {

				if (isCollectibleSet(setName)) {

					final CardDto[] dtos = JS.unwrapArray(CardDto.class, (JSArray<CardDto>) JS.get(sets, JS.wrap(setName)));

					for (final CardDto dto : dtos) {

						/* filter non-deck-building cards */
						if ("Enchantment".equals(dto.getType())) continue;
						if ("Hero".equals(dto.getType())) continue;
						if ("Hero Power".equals(dto.getType())) continue;
						final Boolean collectible = TBone.asBoolean(dto, "collectible");
						if (collectible == null) continue;

						cards.add(dto);

						/* fix properties */
						dto.setSetName(setName);

						final Boolean elite = TBone.asBoolean(dto, "elite");
						dto.setElite(elite != null);

						if (TBone.asString(dto, "playerClass") == null) {
							dto.setPlayerClass("Neutral");
						}

						dto.setDustCost(calcDustCost(dto));

						/* build full text search field */
						final String name = dto.getName();
						final String text = TBone.asStringNotNull(dto, "text");
						final String type = dto.getType();
						final String race = TBone.asStringNotNull(dto, "race");
						final String rarity = TBone.asStringNotNull(dto, "rarity");
						final String fulltext = name + " " + text + " " + type + " " + race + " " + rarity;
						dto.setFullText(fulltext.toLowerCase());

						if ("Minion".equals(type)) {
							cardListByAttack.add(dto);
							cardListByHealth.add(dto);
						}
					}
					Collections.sort(cardListByAttack, (a, b) -> compareByAttack(a, b));
					Collections.sort(cardListByHealth, (a, b) -> compareByHealth(a, b));
				}
			}

			/* sort by cost and name */
				Collections.sort(cards, (a, b) -> {
					if (a.getCost() != b.getCost()) return Integer.compare(a.getCost(), b.getCost());
					return a.getName().compareTo(b.getName());
				});

				System.out.println("Card DB ready"); /* TODO send event */
				cardList = cards.toArray(new CardDto[0]);
				for (final Listener l : listeners) {
					l.execute(this);
				}
			});
	}

	private int compareByAttack(final CardDto a, final CardDto b) {
		final int diffAttack = a.getAttack() - b.getAttack();
		if (diffAttack != 0) return diffAttack;
		final int diffCost = a.getCost() - b.getCost();
		if (diffCost != 0) return diffCost;
		return a.getName().compareTo(b.getName());
	}

	private int compareByHealth(final CardDto a, final CardDto b) {
		final int diffHealth = a.getHealth() - b.getHealth();
		if (diffHealth != 0) return diffHealth;
		final int diffCost = a.getCost() - b.getCost();
		if (diffCost != 0) return diffCost;
		return a.getName().compareTo(b.getName());
	}

	// ====================================================================================================
	// public interface
	// ====================================================================================================

	int calcDustCost(final CardDto dto) {
		if (!isDustSet(dto.getSetName())) return 0;

		// @formatter:off
		if (TBone.asStringNotNull(dto, "rarity") == null) return 0;
		switch (dto.getRarity()) {
			case "Free": return 0;
			case "Common": return 40;
			case "Rare": return 100;
			case "Epic": return 400;
			case "Legendary": return 1600;
			default : return 0;
		}
		// @formatter:on
	}

	boolean isDustSet(final String setName) {
		switch (setName) {
		case "Classic":
		case "Goblins vs Gnomes":
		case "The Grand Tournament":
			return true;
		}
		return false;
	}

	boolean isCollectibleSet(final String setName) {
		switch (setName) {
		case "Basic":
		case "Blackrock Mountain":
		case "Classic":
		case "Curse of Naxxramas":
		case "Goblins vs Gnomes":
		case "Promotion":
		case "Reward":
		case "The Grand Tournament":
			return true;
		}
		return false;
	}

	// ====================================================================================================
	// public interface
	// ====================================================================================================

	public void listen(final Listener listener) {
		if (cardList == null) {
			listeners.add(listener);
		} else {
			listener.execute(this);
		}
	}

	public void forEach(final Consumer consumer) {
		for (final CardDto dto : cardList) {
			consumer.consume(dto);
		}
	}

	public void forEachByAttack(final Consumer consumer) {
		for (final CardDto dto : cardListByAttack) {
			consumer.consume(dto);
		}
	}

	public void forEachByHealth(final Consumer consumer) {
		for (final CardDto dto : cardListByHealth) {
			consumer.consume(dto);
		}
	}

	// language: zhtw, enus
	public static String imageUrl(final String language, final String cardId) {
		return "http://wow.zamimg.com/images/hearthstone/cards/" + language + "/medium/" + cardId + ".png";
	}

}
