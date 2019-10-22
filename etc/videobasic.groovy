import com.skku.nutube.video.cbf.*
import org.lenskit.api.ItemScorer

// the core: use our item scorer
bind ItemScorer to VideoItemScorer
// with the basic profile builder
bind UserProfileBuilder to VideoUserProfileBuilder