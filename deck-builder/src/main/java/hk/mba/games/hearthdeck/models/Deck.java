package hk.mba.games.hearthdeck.models;

import hk.mba.games.hearthdeck.Client;
import hk.mba.games.tbone.TBone;

import java.util.ArrayList;
import java.util.List;

import org.teavm.dom.browser.Storage;
import org.teavm.jso.JS;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public class Deck {

	public String deckId;
	public String name;
	public final String playerClass;

	public final JSObject cards;
	public final int[] manaCurve;

	public int dustCost;
	public int manaCost;

	public int total;
	public int spellTotal;
	public int minionTotal;
	public int weaponTotal;

	public int varity;
	public int spellVarity;
	public int minionVarity;
	public int weaponVarity;

	public Deck(final String deckId, final String playerClass) {
		this.playerClass = playerClass;
		this.deckId = deckId;
		cards = TBone.globals.newObject();
		manaCurve = new int[8];
		total = 0;

		if (deckId == null) name = "Custom " + playerClass;
	}

	public boolean add(final CardDto card) {
		// if (size >= 30) return false; // do not check

		DeckCard data = retrieveCard(card.getId());

		if (data == null) {
			data = (DeckCard) TBone.globals.newObject();
			data.setCardDto(card);
			data.setCount(1);
			data.setGroup(total < 30 ? "Support" : "Alternative");
			insertCard(card.getId(), data);

			varity++;
			// @formatter:off
			switch (card.getType()) {
				case "Weapon" : weaponVarity++; break;
				case "Minion" : minionVarity++; break;
				case "Spell" : spellVarity++; break;
			}
			// @formatter:on

		} else {
			if (card.isElite()) return false; // only 1 elite per deck
			if (data.getCount() >= 2) return false; // max 2 cards per deck
			data.setCount(2);
		}

		if (!"Alternative".equals(data.getGroup())) tallyUp(card, 1);
		// else Alternative, do not update accounting

		return true;
	}

	public boolean remove(final CardDto card) {
		final DeckCard data = retrieveCard(card.getId());
		if (data == null) return false;

		if (data.getCount() == 2) {
			data.setCount(1);
		} else {
			deleteCard(card.getId());
			varity--;
			// @formatter:off
			switch (card.getType()) {
				case "Weapon" : weaponVarity--; break;
				case "Minion" : minionVarity--; break;
				case "Spell" : spellVarity--; break;
			}
			// @formatter:on
		}

		if (!"Alternative".equals(data.getGroup())) tallyDown(card, 1);
		// else Alternative, do not update accounting
		return true;
	}

	public void forEach(final DeckCardConsumer deckCardConsumer) {
		for (final String cardId : TBone.keys(cards)) {
			final DeckCard card = retrieveCard(cardId);
			if (card == null) continue;
			deckCardConsumer.consume(card);
		}
	}

	public String[] cardSets() {

		final JSObject sets = TBone.globals.newObject();

		for (final String cardId : TBone.keys(cards)) {
			final DeckCard card = retrieveCard(cardId);
			if (card == null) continue;
			JS.set(sets, JS.wrap(card.getCardDto().getSetName()), JS.wrap(true));
		}
		return TBone.keys(sets);
	}

	private void insertCard(final String cardId, final DeckCard data) {
		JS.set(cards, JS.wrap(cardId), data);
	}

	private DeckCard retrieveCard(final String cardId) {
		final DeckCard d = (DeckCard) JS.get(cards, JS.wrap(cardId));
		if (JS.isUndefined(d)) return null;
		return d;
	}

	private void deleteCard(final String cardId) {
		JS.set(cards, JS.wrap(cardId), TBone.undefined());
	}

	public boolean contains(final String cardId) {
		final DeckCard d = (DeckCard) JS.get(cards, JS.wrap(cardId));
		if (JS.isUndefined(d)) return false;
		return true;
	}

	// ====================================================================================================
	// Card grouping
	// ====================================================================================================

	public boolean rotateGroup(final CardDto card) {
		final DeckCard data = retrieveCard(card.getId());
		if (data == null) return false;

		// @formatter:off
		switch (data.getGroup()) {
			case "Core" : data.setGroup("Alternative"); tallyDown(data.getCardDto(), data.getCount()); break;
			case "Answer" : data.setGroup("Core"); break;
			case "Support" : data.setGroup("Answer"); break;
			case "Alternative" : data.setGroup("Support"); tallyUp(data.getCardDto(), data.getCount()); break;
		}
		// @formatter:on

		return true;
	}

	public void setGroup(final CardDto card, final String group) {

		int counter = 0;

		final DeckCard data = retrieveCard(card.getId());
		if (data == null) return;

		do {
			if (group.equals(data.getGroup())) return;
			rotateGroup(card);
			counter++;
		} while (counter < 5);

		if (!"Alternative".equals(data.getGroup())) {
			data.setGroup("Alternative");
			tallyDown(data.getCardDto(), data.getCount());
		}
	}

	// ====================================================================================================
	// Accounting
	// ====================================================================================================

	private void tallyUp(final CardDto card, final int count) {
		for (int i = 0; i < count; i++) {
			total++;
			dustCost += card.getDustCost();
			manaCost += card.getCost();

			final int manaCurveIndex = (card.getCost() > 7) ? 7 : card.getCost();
			manaCurve[manaCurveIndex]++;

			// @formatter:off
			switch (card.getType()) {
				case "Weapon" : weaponTotal++; break;
				case "Minion" : minionTotal++; break;
				case "Spell" : spellTotal++; break;
			}
			// @formatter:on
		}
	}

	private void tallyDown(final CardDto card, final int count) {
		for (int i = 0; i < count; i++) {
			total--;
			dustCost -= card.getDustCost();
			manaCost -= card.getCost();

			final int manaCurveIndex = (card.getCost() > 7) ? 7 : card.getCost();
			manaCurve[manaCurveIndex]--;

			// @formatter:off
			switch (card.getType()) {
				case "Weapon" : weaponTotal--; break;
				case "Minion" : minionTotal--; break;
				case "Spell" : spellTotal--; break;
			}
			// @formatter:on
		}
	}

	// ====================================================================================================
	// DAO
	// ====================================================================================================

	public void load(final Runnable runnable) {
		if (deckId != null) {
			final Storage storage = TBone.globals.getLocalStorage();
			final DeckDto dto = TBone.parseJson(storage.getItem("deck:" + deckId));

			name = dto.getName();
			Client.cardDb.listen(db -> {

				final JSObject cards = dto.getCards();
				db.forEach(cardDto -> {
					final Deck.DeckCard deckCard = (Deck.DeckCard) JS.get(cards, JS.wrap(cardDto.getId()));
					if (!TBone.isUndefined(deckCard)) {
						for (int i = 0; i < deckCard.getCount(); i++) {
							add(cardDto);
							setGroup(cardDto, deckCard.getGroup());
						}
					}
				});

				runnable.run();
			});
		}
	}

	// TODO change getStatusText() to getStatus() after TeaVM is upgraded
	public void save() {

		final Storage storage = TBone.globals.getLocalStorage();

		final DeckDto dto = prepareDeckDto();

		if (deckId == null) {
			deckId = playerClass + ":" + System.currentTimeMillis();
			dto.setId(deckId);
			storage.setItem("deck:" + dto.getId(), TBone.stringify(dto));

			Client.gotoDeck(deckId);
		} else {
			storage.setItem("deck:" + dto.getId(), TBone.stringify(dto));
		}
	}

	private DeckDto prepareDeckDto() {

		final DeckDto dto = (DeckDto) TBone.globals.newObject();

		if (deckId != null) dto.setId(deckId);
		dto.setPlayerClass(playerClass);
		dto.setName(name);

		dto.setDeckSize(total);
		dto.setVarities(varity);

		dto.setManaCost(manaCost);
		dto.setDustCost(dustCost);

		dto.setMinions(minionTotal);
		dto.setSpells(spellTotal);
		dto.setWeapons(weaponTotal);

		final JSObject cards = TBone.globals.newObject();
		forEach(deckCard -> {

			final JSObject item = TBone.globals.newObject();
			JS.set(item, JS.wrap("count"), JS.wrap(deckCard.getCount()));
			JS.set(item, JS.wrap("group"), JS.wrap(deckCard.getGroup()));

			final String id = deckCard.getCardDto().getId();
			JS.set(cards, JS.wrap(id), item);
		});
		dto.setCards(cards);

		return dto;
	}

	public static void listDto(final ListDeckConsumer consumer) {

		final Storage storage = TBone.globals.getLocalStorage();

		final List<DeckDto> list = new ArrayList<>();

		for (int i = 0; i < storage.getLength(); i++) {
			final String key = storage.key(i);
			if (key.startsWith("deck:")) {
				final DeckDto dto = TBone.parseJson(storage.getItem(key));
				list.add(dto);
			}
		}
		consumer.consume(list);
	}

	// ====================================================================================================
	// interfaces
	// ====================================================================================================

	public static interface DeckCardConsumer {
		void consume(DeckCard deckCard);
	}

	public static interface ListDeckConsumer {
		void consume(List<DeckDto> deck);
	}

	public static interface DeckCard extends JSObject {

		@JSProperty
		public CardDto getCardDto();

		@JSProperty
		public void setCardDto(CardDto card);

		@JSProperty
		public int getCount();

		@JSProperty
		public void setCount(int count);

		@JSProperty
		public String getGroup();

		@JSProperty
		public void setGroup(String group);

	}
}
