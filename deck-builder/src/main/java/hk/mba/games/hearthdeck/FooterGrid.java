package hk.mba.games.hearthdeck;

import static hk.mba.games.tbone.elements.SemanticUi.*;
import hk.mba.games.tbone.View;
import hk.mba.games.tbone.elements.UiNode;

public class FooterGrid extends View {

	@Override
	public UiNode<?> onRender() {

		return grid("page").add(
				divider("hidden"),
				row()
						.add(column()
								.add(divider())
								.add(span("© AllStarCVs Technologies Limited 2015"))
						));
	}
}
