import java.util.ArrayList;
import java.util.HashMap;

public class KNNC {

	public static int K;

	public static double normalized_training_attr[][];
	public static double normalized_test_attr[][];
	public static double mean_classwise[][];
	public static double mean_training_attr_wise[];
	public static double mean_test_attr_wise[];
	public static double std_classwise[][];
	public static double std_training_attr_wise[];
	public static double std_test_attr_wise[];
	public static double sum_class[][];
	public static double sum_diff_square[][];

	public static int class_samples_cnt[][];

	public double mMinDistance = 0.0;

	public static ArrayList<Integer> tiedClassList = new ArrayList<Integer>();

	public void handleKNNC(int k) {

		K = k;

		System.out.println("handling NNC with K:" + K);

		initVariables();
		calculateMeanNStd();
		normalizeTrainingData();
		normalizeTestData();
		classifyTestData();

	}

	private void classifyTestData() {

		double classification_accuracy = 0.0;
		double sum_accuracy = 0.0;
		
		for (int test_object_no = 0; test_object_no < knnclassify.TEST_OBJECT_COUNT; test_object_no++) {

			int nearest_training_object = classifyTestSample(test_object_no);

			int training_class = knnclassify.training_class[nearest_training_object];
			int test_class = knnclassify.test_class[test_object_no];
			// boolean isClassificationSuccessful = (training_class ==
			// test_class);
			boolean isTie = tiedClassList.size() > 1;
			double accuracy = 0.0;

			if (isTie) {
				if (tiedClassList.contains(test_class)) {
					// If there were ties in your classification result, and the
					// correct class was one of the classes that tied for best,
					// the accuracy is 1 divided by the number of classes that
					// tied for best.
					accuracy = 1 / tiedClassList.size();
				}
			} else {
				if (tiedClassList.contains(test_class)) {
					// If there were no ties in your classification result, and
					// the predicted class is correct, the accuracy is 1.
					accuracy = 1.0;
				}
			}
			
			sum_accuracy += accuracy;

			System.out.printf(
					"test_object = %5d, predicted_class = %3d, true_class = %3d, nn = %5d, distance = %7.2f, accuracy = %4.2f\n",
					test_object_no, training_class, test_class, nearest_training_object, mMinDistance, accuracy);
		}
		
		classification_accuracy = sum_accuracy / knnclassify.TEST_OBJECT_COUNT;
		
		System.out.printf("\n\nclassification accuracy = %6.4f\n\n\n", classification_accuracy);
		
	}

	private int classifyTestSample(int test_object_no) {

		mMinDistance = Double.MAX_VALUE;
		int nearest_training_object = 0;

		for (int training_object_no = 0; training_object_no < knnclassify.TRAINING_OBJECT_COUNT; training_object_no++) {
			double curr_euclidean_distance = calculateEuclideanDistance(training_object_no, test_object_no);
			if (curr_euclidean_distance < mMinDistance) {
				tiedClassList.clear();
				tiedClassList.add(knnclassify.training_class[training_object_no]);
				mMinDistance = curr_euclidean_distance;
				nearest_training_object = training_object_no;
			} else if (curr_euclidean_distance == mMinDistance) {
				//come here when two training samples tied for minimum distance
				if (!tiedClassList.contains(knnclassify.training_class[training_object_no])) {
					tiedClassList.add(knnclassify.training_class[training_object_no]);
				}
			}
		}

		return nearest_training_object;
	}

	private double calculateEuclideanDistance(int training_object_no, int test_object_no) {

		double distance = 0.0;
		double diff_square = 0.0;
		double sum_diff_square = 0.0;

		for (int attr_no = 0; attr_no < knnclassify.ATTRIBUTES_COUNT; attr_no++) {
			diff_square = Math.pow((normalized_training_attr[training_object_no][attr_no]
					- normalized_test_attr[test_object_no][attr_no]), 2);
			sum_diff_square += diff_square;
		}

		distance = Math.sqrt(sum_diff_square);

		return distance;
	}

	private void initVariables() {

		normalized_training_attr = new double[knnclassify.TRAINING_OBJECT_COUNT][knnclassify.ATTRIBUTES_COUNT];
		normalized_test_attr = new double[knnclassify.TEST_OBJECT_COUNT][knnclassify.ATTRIBUTES_COUNT];
		mean_training_attr_wise = new double[knnclassify.ATTRIBUTES_COUNT];
		mean_test_attr_wise = new double[knnclassify.ATTRIBUTES_COUNT];
		std_training_attr_wise = new double[knnclassify.ATTRIBUTES_COUNT];
		std_test_attr_wise = new double[knnclassify.ATTRIBUTES_COUNT];

		mean_classwise = new double[knnclassify.ATTRIBUTES_COUNT][knnclassify.CLASS_COUNT];
		std_classwise = new double[knnclassify.ATTRIBUTES_COUNT][knnclassify.CLASS_COUNT];
		sum_class = new double[knnclassify.ATTRIBUTES_COUNT][knnclassify.CLASS_COUNT];
		sum_diff_square = new double[knnclassify.ATTRIBUTES_COUNT][knnclassify.CLASS_COUNT];
		class_samples_cnt = new int[knnclassify.ATTRIBUTES_COUNT][knnclassify.CLASS_COUNT];
	}

	private void calculateMeanNStd() {

		for (int attr_no = 0; attr_no < knnclassify.ATTRIBUTES_COUNT; attr_no++) {
			calculateTrainingAttributeWiseMean(attr_no);
			calculateTrainingAttributeWiseStd(attr_no);
			calculateTestAttributeWiseMean(attr_no);
			calculateTestAttributeWiseStd(attr_no);

		}
	}

	// ********************************** TRAINING DATA ************************************

	private void normalizeTrainingData() {
		for (int attr_no = 0; attr_no < knnclassify.ATTRIBUTES_COUNT; attr_no++) {
			normalizeTrainingAttribute(attr_no);
		}
	}

	private void normalizeTrainingAttribute(int attr_no) {
		for (int object_no = 0; object_no < knnclassify.TRAINING_OBJECT_COUNT; object_no++) {
			normalized_training_attr[object_no][attr_no] = (knnclassify.training_attr[object_no][attr_no]
					- mean_training_attr_wise[attr_no]) / std_training_attr_wise[attr_no];
		}
	}

	private void calculateTrainingAttributeWiseStd(int attr_no) {
		double diff_square = 0.0;
		double sum_diff_square = 0.0;

		for (int object_no = 0; object_no < knnclassify.TRAINING_OBJECT_COUNT; object_no++) {
			diff_square = Math.pow((knnclassify.training_attr[object_no][attr_no] - mean_training_attr_wise[attr_no]),
					2);
			sum_diff_square += diff_square;
		}
		std_training_attr_wise[attr_no] = Math.sqrt(sum_diff_square / (knnclassify.TRAINING_OBJECT_COUNT - 1));
	}

	private void calculateTrainingAttributeWiseMean(int attr_no) {
		double sum = 0.0;
		for (int object_no = 0; object_no < knnclassify.TRAINING_OBJECT_COUNT; object_no++) {
			sum += knnclassify.training_attr[object_no][attr_no];
		}
		mean_training_attr_wise[attr_no] = sum / knnclassify.TRAINING_OBJECT_COUNT;
	}

	private static void printNormalizedTrainingData() {
		System.out.println("Printing normalized training data...\n\n");

		for (int object_no = 0; object_no < knnclassify.TRAINING_OBJECT_COUNT; object_no++) {
			for (int attr_no = 0; attr_no < knnclassify.ATTRIBUTES_COUNT; attr_no++) {
				System.out.printf("%.2f ", normalized_training_attr[object_no][attr_no]);
			}
			System.out.println();
		}
	}

	// ********************************** TEST DATA ************************************

	private void normalizeTestData() {
		for (int attr_no = 0; attr_no < knnclassify.ATTRIBUTES_COUNT; attr_no++) {
			normalizeTestAttribute(attr_no);
		}
	}

	private void normalizeTestAttribute(int attr_no) {
		for (int object_no = 0; object_no < knnclassify.TEST_OBJECT_COUNT; object_no++) {
			normalized_test_attr[object_no][attr_no] = (knnclassify.test_attr[object_no][attr_no]
					- mean_test_attr_wise[attr_no]) / std_test_attr_wise[attr_no];
		}
	}

	private void calculateTestAttributeWiseStd(int attr_no) {
		double diff_square = 0.0;
		double sum_diff_square = 0.0;

		for (int object_no = 0; object_no < knnclassify.TEST_OBJECT_COUNT; object_no++) {
			diff_square = Math.pow((knnclassify.test_attr[object_no][attr_no] - mean_test_attr_wise[attr_no]), 2);
			sum_diff_square += diff_square;
		}
		std_test_attr_wise[attr_no] = Math.sqrt(sum_diff_square / (knnclassify.TEST_OBJECT_COUNT - 1));
	}

	private void calculateTestAttributeWiseMean(int attr_no) {
		double sum = 0.0;
		for (int object_no = 0; object_no < knnclassify.TEST_OBJECT_COUNT; object_no++) {
			sum += knnclassify.test_attr[object_no][attr_no];
		}
		mean_test_attr_wise[attr_no] = sum / knnclassify.TEST_OBJECT_COUNT;
	}

	private static void printNormalizedTestData() {
		System.out.println("Printing normalized test data...\n\n");

		for (int object_no = 0; object_no < knnclassify.TEST_OBJECT_COUNT; object_no++) {
			for (int attr_no = 0; attr_no < knnclassify.ATTRIBUTES_COUNT; attr_no++) {
				System.out.printf("%.2f ", normalized_test_attr[object_no][attr_no]);
			}
			System.out.println();
		}
	}
}
