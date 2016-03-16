/**
 * Created by joey on 16-3-14.
 */
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class Memcached {
    protected static MemCachedClient mc = new MemCachedClient();
    static {
        String[] serverlist = {
                "127.0.0.1:11211"
        };
        SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(serverlist);
        pool.initialize();
    }
}
