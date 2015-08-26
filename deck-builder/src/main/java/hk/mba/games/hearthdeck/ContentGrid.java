package hk.mba.games.hearthdeck;

import static hk.mba.games.tbone.elements.SemanticUi.*;
import hk.mba.games.tbone.View;
import hk.mba.games.tbone.elements.UiNode;

public class ContentGrid extends View {

	@Override
	public UiNode<?> onRender() {
		return grid("page").attr("id", "mainContent");// add ID for mockups
	}
}
