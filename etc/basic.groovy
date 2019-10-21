import com.skku.nutube.cbf.TFIDFItemScorer
import com.skku.nutube.cbf.ThresholdUserProfileBuilder
import com.skku.nutube.cbf.UserProfileBuilder
import org.lenskit.api.ItemScorer

// the core: use our item scorer
bind ItemScorer to TFIDFItemScorer
// with the basic profile builder
bind UserProfileBuilder to ThresholdUserProfileBuilder