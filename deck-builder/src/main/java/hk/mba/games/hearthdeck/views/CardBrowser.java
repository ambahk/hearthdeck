package hk.mba.games.hearthdeck.views;

import static hk.mba.games.tbone.elements.SemanticUi.*;
import hk.mba.games.hearthdeck.Client;
import hk.mba.games.hearthdeck.models.CardDb;
import hk.mba.games.hearthdeck.models.CardDb.PlayerClass;
import hk.mba.games.tbone.View;
import hk.mba.games.tbone.elements.UiCommon;
import hk.mba.games.tbone.elements.UiNode;

public class CardBrowser extends View {

	private final UiCommon cards = grid(5, "compact");
	private final UiCommon playerClassButtons = buttons(CardDb.PlayerClass.values().length, "fluid basic blue");

	private PlayerClass currentFilter = CardDb.PlayerClass.Neutral;

	@Override
	public UiNode<?> onRender() {

		for (final CardDb.PlayerClass playerClass : CardDb.PlayerClass.values()) {

			final String active = (playerClass.equals(currentFilter)) ? "active" : "";
			playerClassButtons.add(
					button(active)
							.data("playerclass", playerClass.name())
							.text(playerClass.name())
							.click(e -> filterByClass(playerClass))
					);
		}

		return grid(1, "page").add(
				column("fluid").add(playerClassButtons),
				column().add(cards)
				).ready(() -> filterByClass(currentFilter));
	}

	private void filterByClass(final PlayerClass playerClass) {

		currentFilter = playerClass;

		playerClassButtons.jquery()
				.children()
				.removeClass("active")
				.filter("[data-playerclass='" + playerClass + "']")
				.addClass("active");

		cards.clear();
		Client.cardDb.listen(db -> {
			db.forEach(dto -> {

				if (!dto.isCollectible()) return;
				if (!playerClass.name().equals(dto.getPlayerClass())) return;

				cards.add(column().add(image(CardDb.imageUrl("enus", dto.getId()))));
			});
		});
	}
}
