package hk.mba.games.hearthdeck.views;

import static hk.mba.games.tbone.elements.SemanticUi.*;
import hk.mba.games.hearthdeck.Client;
import hk.mba.games.hearthdeck.models.Deck;
import hk.mba.games.hearthdeck.models.DeckDto;
import hk.mba.games.tbone.View;
import hk.mba.games.tbone.elements.UiCommon;
import hk.mba.games.tbone.elements.UiDropdown;
import hk.mba.games.tbone.elements.UiNode;

import java.util.Collections;

public class DeckBrowser extends View {

	public static enum SortOption {
		Name, Hero, Varities, Mana, Dust
	}

	// components
	private UiCommon deckListBody;

	@Override
	protected UiNode<?> onRender() {

		final UiDropdown heroesSelect = selectionDropdown(null, v -> Client.gotoDeckBuilder(v.value()))
				.text("Craft a new deck for...")
				.options("Druid", "Hunter", "Mage", "Paladin", "Priest", "Rogue", "Shaman", "Warrior", "Warlock");

		final UiCommon createDeckPanel = segment().add(heroesSelect);

		final UiCommon deckListPanel = segment().add(
				h1("Decks"),
				table("striped").add(
						thead().add(tr().add(
								th(6).text("Name").click(e -> showSorted(SortOption.Name)).style("cursor:pointer"),
								th(2).text("Hero").click(e -> showSorted(SortOption.Hero)).style("cursor:pointer"),
								th(2).text("Varities").click(e -> showSorted(SortOption.Varities)).style("cursor:pointer"),
								th(2).text("Mana Cost").click(e -> showSorted(SortOption.Mana)).style("cursor:pointer"),
								th(2).text("Dust Cost").click(e -> showSorted(SortOption.Dust)).style("cursor:pointer"),
								th(2).text("M/S/W")
								)),
						deckListBody = tbody()
						)
				);

		return grid(1, "page").add(column().add(
				createDeckPanel,
				deckListPanel
				)).ready(() -> showSorted(SortOption.Name));
	}

	private void showSorted(final SortOption sortOption) {
		deckListBody.clear();

		Deck.listDto(list -> {

			switch (sortOption) {
			case Dust:
				Collections.sort(list, (a, b) -> a.getDustCost() - b.getDustCost());
				break;

			case Hero:
				Collections.sort(list, (a, b) -> a.getPlayerClass().compareTo(b.getPlayerClass()));
				break;

			case Mana:
				Collections.sort(list, (a, b) -> a.getManaCost() - b.getManaCost());
				break;

			case Name:
				Collections.sort(list, (a, b) -> a.getName().compareTo(b.getName()));
				break;

			case Varities:
				Collections.sort(list, (a, b) -> a.getVarities() - b.getVarities());
				break;
			}

			for (final DeckDto dto : list) {
				deckListBody.add(tr().add(
						td().text(dto.getName()),
						td().text("" + dto.getPlayerClass()),
						td().text("" + dto.getVarities()),
						td().text("" + dto.getManaCost()),
						td().text("" + dto.getDustCost()),
						td().text(dto.getMinions() + "/" + dto.getSpells() + "/" + dto.getWeapons())
						).click(e -> Client.gotoDeck(dto.getId())).style("cursor:pointer"));
			}
		});
	}
}
