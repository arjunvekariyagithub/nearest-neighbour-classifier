import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class knnclassify {

	public static final String PEN = "pen";
	public static final String SATELLITE = "satellite";
	public static final String YEAST = "yeast";

	public static final int INDEX_TRAINING_FILE = 0;
	public static final int INDEX_TEST_FILE = 1;
	public static final int INDEX_K_COUNT = 2;

	public static final int PENDIGITS_TRAINING_OBJECT_COUNT = 7494;
	public static final int PENDIGITS_TEST_OBJECT_COUNT = 3498;
	public static final int PENDIGITS_ATTRIBUTES_COUNT = 16;
	public static final int PENDIGITS_CLASS_COUNT = 10;

	public static final int SATELLITE_TRAINING_OBJECT_COUNT = 4435;
	public static final int SATELLITE_TEST_OBJECT_COUNT = 2000;
	public static final int SATELLITE_ATTRIBUTES_COUNT = 36;
	public static final int SATELLITE_CLASS_COUNT = 7;

	public static final int YEAST_TRAINING_OBJECT_COUNT = 1000;
	public static final int YEAST_TEST_OBJECT_COUNT = 484;
	public static final int YEAST_ATTRIBUTES_COUNT = 8;
	public static final int YEAST_CLASS_COUNT = 10;

	public static int TRAINING_OBJECT_COUNT;
	public static int TEST_OBJECT_COUNT;
	public static int ATTRIBUTES_COUNT;
	public static int CLASS_COUNT;

	public static double training_attr[][];
	public static double test_attr[][];
	public static int training_class[];
	public static int test_class[];

	public static BufferedReader inputStream;

	public static boolean isPenDigitCase;
	public static boolean isSatelliteCase;
	public static boolean isYeastCase;

	
	public static KNNC knnc = new KNNC();

	public static void main(String[] args) {

		if (args == null | args.length <= 0) {
			System.out.println("Oops..submit arguments please!!");
			return;
		}

		checkCase(args);

		if (!isValidCase()) {
			System.out.println("Oops..invalid arguments!!");
			return;
		}

		initVariables();

		System.out.println("reading data..");
		if (readTrainingData(args[INDEX_TRAINING_FILE]) && readTestData(args[INDEX_TEST_FILE])) {
				try {
					knnc.handleKNNC(Integer.parseInt(args[INDEX_K_COUNT]));
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Oops..K value is missing!!");
				}
		} else {
			System.out.println("Oops..invalid arguments!!");
		}
	}

	private static boolean isValidCase() {
		return (isPenDigitCase || isSatelliteCase || isYeastCase);
	}

	private static void initVariables() {
		if (isPenDigitCase) {

			TRAINING_OBJECT_COUNT = PENDIGITS_TRAINING_OBJECT_COUNT;
			TEST_OBJECT_COUNT = PENDIGITS_TEST_OBJECT_COUNT;
			ATTRIBUTES_COUNT = PENDIGITS_ATTRIBUTES_COUNT;
			CLASS_COUNT = PENDIGITS_CLASS_COUNT;

		} else if (isSatelliteCase) {

			TRAINING_OBJECT_COUNT = SATELLITE_TRAINING_OBJECT_COUNT;
			TEST_OBJECT_COUNT = SATELLITE_TEST_OBJECT_COUNT;
			ATTRIBUTES_COUNT = SATELLITE_ATTRIBUTES_COUNT;
			CLASS_COUNT = SATELLITE_CLASS_COUNT;

		} else if (isYeastCase) {

			TRAINING_OBJECT_COUNT = YEAST_TRAINING_OBJECT_COUNT;
			TEST_OBJECT_COUNT = YEAST_TEST_OBJECT_COUNT;
			ATTRIBUTES_COUNT = YEAST_ATTRIBUTES_COUNT;
			CLASS_COUNT = YEAST_CLASS_COUNT;
		}

		training_attr = new double[TRAINING_OBJECT_COUNT][ATTRIBUTES_COUNT];
		test_attr = new double[TEST_OBJECT_COUNT][ATTRIBUTES_COUNT];
		training_class = new int[TRAINING_OBJECT_COUNT];
		test_class = new int[TEST_OBJECT_COUNT];
	}

	private static void checkCase(String[] args) {
		String fileName = args[INDEX_TRAINING_FILE];

		if (fileName.contains(PEN)) {
			isPenDigitCase = true;
		} else if (fileName.contains(SATELLITE)) {
			isSatelliteCase = true;
		} else if (fileName.contains(YEAST)) {
			isYeastCase = true;
		} else {
			System.out.println("Oops..invalid arguments!!");
		}
	}

	private static void printTrainingData() {
		System.out.println("printing training data...\n\n");

		for (int object_no = 0; object_no < TRAINING_OBJECT_COUNT; object_no++) {
			for (int attr_no = 0; attr_no < ATTRIBUTES_COUNT; attr_no++) {
				System.out.print(training_attr[object_no][attr_no] + "  ");
			}
			System.out.print(training_class[object_no]);
			System.out.println();
		}
	}

	private static void printTestData() {
		System.out.println("printing test data...\n\n");

		for (int object_no = 0; object_no < TEST_OBJECT_COUNT; object_no++) {
			for (int attr_no = 0; attr_no < ATTRIBUTES_COUNT; attr_no++) {
				System.out.print(test_attr[object_no][attr_no] + "  ");
			}
			System.out.print(test_class[object_no]);
			System.out.println();
		}
	}

	private static boolean readTestData(String fileName) {
		System.out.println("reading test data from : " + fileName);
		
		String line;
		try {
			inputStream = new BufferedReader(new FileReader(fileName));
			Scanner scan;
			int object_no = 0;
			while ((line = inputStream.readLine()) != null) {
				if (!(line.equals(""))) {
					scan = new Scanner(line);
					int attr_read_count = 0;
					while (scan.hasNextDouble()) {
						if (attr_read_count == ATTRIBUTES_COUNT) {
							test_class[object_no] = scan.nextInt();
						} else {
							test_attr[object_no][attr_read_count] = scan.nextDouble();
						}
						attr_read_count++;
					}
				}
				object_no++;
			}

		} catch (Exception ex) {
			System.out.println("Exception:- " + ex.toString());
			return false;
		}

		//printTestData();
		
		return true;
	}

	private static boolean readTrainingData(String fileName) {
		System.out.println("reading trainig data from : " + fileName);

		String line;
		try {
			inputStream = new BufferedReader(new FileReader(fileName));
			Scanner scan;
			int object_no = 0;
			while ((line = inputStream.readLine()) != null) {
				if (!(line.equals(""))) {
					scan = new Scanner(line);
					int attr_read_count = 0;
					while (scan.hasNextDouble()) {
						if (attr_read_count == ATTRIBUTES_COUNT) {
						    training_class[object_no] = scan.nextInt();
						} else {
							training_attr[object_no][attr_read_count] = scan.nextDouble();
						}
						attr_read_count++;
					}
				}
				object_no++;
			}

		} catch (Exception ex) {
			System.out.println("Exception:- " + ex.toString());
			return false;
		}
		
		//printTrainingData();

		return true;
	}
}