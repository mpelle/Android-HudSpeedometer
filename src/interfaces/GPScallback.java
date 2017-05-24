package interfaces;

import android.location.Location;

public interface GPScallback
{
        public abstract void onGPSUpdate(Location location);
}