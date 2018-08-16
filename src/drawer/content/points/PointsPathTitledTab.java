package drawer.content.points;

import drawer.content.PathTitledTab;
import drawer.curves.PointsPathGroup;

public class PointsPathTitledTab extends PathTitledTab<PointsPathGroup> {

  public PointsPathTitledTab() {
    super(new PointsPathGroup());
  }

  public void toggleShowingVelocity() {
    setShowingVelocities(!getPointsPathGroup().getDrawer().isShowVelocities());
  }

  public void setShowingVelocities(boolean showVelocity) {
    getPointsPathGroup().getDrawer().setShowVelocities(showVelocity);
  }
}
