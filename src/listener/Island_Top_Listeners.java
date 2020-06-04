package listener;

import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.events.IslandPreLevelEvent;
import main.Helper_Methods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class Island_Top_Listeners extends Helper_Methods implements Listener
{
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onIslandUpdate(IslandPreLevelEvent event)
    {
        int levelCost = Settings.levelCost;
        int newValue = (int) (event.getLongLevel() + getBlockValueAtLocation(event.getPlayer()));
        double d = getBlockValueAtLocation(event.getPlayer());
        double x = d - Math.floor(d);
        double newCost = x * levelCost;
        long pointsTonextLevel = (long) (event.getLongPointsToNextLevel() - newCost);

        if (pointsTonextLevel > 0)
        {
            event.setLevel(newValue);
            event.setPointsToNextLevel((int) pointsTonextLevel);
        }
        else
        {
            if (pointsTonextLevel < 0)
            {
                pointsTonextLevel *= -1;
                long newPointstoNextLevel = levelCost - pointsTonextLevel;
                event.setPointsToNextLevel((int) newPointstoNextLevel);
                event.setLevel(newValue + 1);
            }
        }
    }
}
