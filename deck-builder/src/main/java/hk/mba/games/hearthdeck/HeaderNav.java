package hk.mba.games.hearthdeck;

import static hk.mba.games.tbone.elements.SemanticUi.*;
import hk.mba.games.tbone.View;
import hk.mba.games.tbone.elements.UiNode;

public class HeaderNav extends View {

	@Override
	public UiNode<?> onRender() {

		return UiNode.element("nav")
				.attr("class", "ui fixed menu inverted navbar page grid").add(
						item("link").text("Cards").click(e -> Client.gotoCards()),
						item("link").text("Decks").click(e -> Client.gotoDecks()),
						item("link").text("Teams").click(e -> Client.gotoTeams())
				);
	}
}
