/*
 *
 */
package com.lppbpl;
// Generated by proto2javame, Thu May 02 15:16:35 IST 2013.

import net.jarlehansen.protobuf.javame.AbstractOutputWriter;
import net.jarlehansen.protobuf.javame.ComputeSizeUtil;
import net.jarlehansen.protobuf.javame.UninitializedMessageException;
import net.jarlehansen.protobuf.javame.input.DelimitedInputStream;
import net.jarlehansen.protobuf.javame.input.DelimitedSizeUtil;
import net.jarlehansen.protobuf.javame.input.InputReader;
import net.jarlehansen.protobuf.javame.input.taghandler.DefaultUnknownTagHandlerImpl;
import net.jarlehansen.protobuf.javame.input.taghandler.UnknownTagHandler;
import net.jarlehansen.protobuf.javame.output.OutputWriter;

import java.io.IOException;
import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class Test.
 */
public final class Test extends AbstractOutputWriter {

	/** The unknown tag handler. */
	private static UnknownTagHandler unknownTagHandler = DefaultUnknownTagHandlerImpl.newInstance();

	/** The message type. */
	private final int messageType;

	/** The Constant fieldNumberMessageType. */
	private static final int fieldNumberMessageType = 1;

	/** The test type. */
	private final int testType;

	/** The Constant fieldNumberTestType. */
	private static final int fieldNumberTestType = 2;

	/** The has test type. */
	private final boolean hasTestType;

	/** The simulation type. */
	private final int simulationType;

	/** The Constant fieldNumberSimulationType. */
	private static final int fieldNumberSimulationType = 3;

	/** The has simulation type. */
	private final boolean hasSimulationType;

	/** The ecg data. */
	private final EcgData ecgData;

	/** The Constant fieldNumberEcgData. */
	private static final int fieldNumberEcgData = 5;

	/** The has ecg data. */
	private final boolean hasEcgData;


	/**
	 * Method newBuilder.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructor for Test.
	 * @param builder Builder
	 */
	private Test(final Builder builder) {
		if (builder.hasMessageType ) {
			this.messageType = builder.messageType;
			this.testType = builder.testType;
			this.hasTestType = builder.hasTestType;
			this.simulationType = builder.simulationType;
			this.hasSimulationType = builder.hasSimulationType;
			this.ecgData = builder.ecgData;
			this.hasEcgData = builder.hasEcgData;
		} else {
			throw new UninitializedMessageException("Not all required fields were included (false = not included in message), " +
				" messageType:" + builder.hasMessageType + "");
		}
	}

	/**
	 * The Class Simulation.
	 */
	public static class Simulation {

		/** The Constant SIMULATE_TRIGGER_BUTTON_PRESSED. */
		public static final int SIMULATE_TRIGGER_BUTTON_PRESSED = 1;

		/** The Constant SIMULATE_TRIGGER_BUTTON_RELEASED. */
		public static final int SIMULATE_TRIGGER_BUTTON_RELEASED = 2;

		/** The Constant SIMULATE_FACTORY_RESET_BUTTON_PRESSED. */
		public static final int SIMULATE_FACTORY_RESET_BUTTON_PRESSED = 3;

		/** The Constant SIMULATE_STRIP_INSERT. */
		public static final int SIMULATE_STRIP_INSERT = 4;

		/** The Constant SIMULATE_STRIP_REMOVED. */
		public static final int SIMULATE_STRIP_REMOVED = 5;

		/**
		 * Method getStringValue.
		 * @param value int
		 * @return String
		 */
		public static String getStringValue(int value) {
			String retValue;

			if (value == 1) {
				retValue = "SIMULATE_TRIGGER_BUTTON_PRESSED";

			} else if (value == 2) {
				retValue = "SIMULATE_TRIGGER_BUTTON_RELEASED";

			} else if (value == 3) {
				retValue = "SIMULATE_FACTORY_RESET_BUTTON_PRESSED";

			} else if (value == 4) {
				retValue = "SIMULATE_STRIP_INSERT";

			} else if (value == 5) {
				retValue = "SIMULATE_STRIP_REMOVED";

			} else {
				retValue = "";

			}

			return retValue;
		}
	}

	/**
	 * The Class TestType.
	 */
	public static class TestType {

		/** The Constant ARTIFACT_DETECTION_ALGORITHM. */
		public static final int ARTIFACT_DETECTION_ALGORITHM = 1;

		/** The Constant ECG_FILTER_ALGORITHM. */
		public static final int ECG_FILTER_ALGORITHM = 2;

		/** The Constant ACTIVITY_ALGORITHM. */
		public static final int ACTIVITY_ALGORITHM = 3;

		/** The Constant BG_BIST. */
		public static final int BG_BIST = 4;

		/** The Constant ADC_READ. */
		public static final int ADC_READ = 5;

		/** The Constant ACCELEROMETER_WHOAMI. */
		public static final int ACCELEROMETER_WHOAMI = 6;

		/** The Constant SF_BIST. */
		public static final int SF_BIST = 7;

		/** The Constant ECG_SENSOR_POWER_ON. */
		public static final int ECG_SENSOR_POWER_ON = 8;

		/** The Constant ECG_SENSOR_POWER_OFF. */
		public static final int ECG_SENSOR_POWER_OFF = 9;

		/**
		 * Method getStringValue.
		 * @param value int
		 * @return String
		 */
		public static String getStringValue(int value) {
			String retValue;

			if (value == 1) {
				retValue = "ARTIFACT_DETECTION_ALGORITHM";

			} else if (value == 2) {
				retValue = "ECG_FILTER_ALGORITHM";

			} else if (value == 3) {
				retValue = "ACTIVITY_ALGORITHM";

			} else if (value == 4) {
				retValue = "BG_BIST";

			} else if (value == 5) {
				retValue = "ADC_READ";

			} else if (value == 6) {
				retValue = "ACCELEROMETER_WHOAMI";

			} else if (value == 7) {
				retValue = "SF_BIST";

			} else if (value == 8) {
				retValue = "ECG_SENSOR_POWER_ON";

			} else if (value == 9) {
				retValue = "ECG_SENSOR_POWER_OFF";

			} else {
				retValue = "";

			}

			return retValue;
		}
	}

	/**
	 * The Class MessageType.
	 */
	public static class MessageType {

		/** The Constant Simulate. */
		public static final int Simulate = 1;

		/** The Constant Validation. */
		public static final int Validation = 2;

		/**
		 * Method getStringValue.
		 * @param value int
		 * @return String
		 */
		public static String getStringValue(int value) {
			String retValue;

			if (value == 1) {
				retValue = "Simulate";

			} else if (value == 2) {
				retValue = "Validation";

			} else {
				retValue = "";

			}

			return retValue;
		}
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The message type. */
		private int messageType;

		/** The has message type. */
		private boolean hasMessageType = false;

		/** The test type. */
		private int testType;

		/** The has test type. */
		private boolean hasTestType = false;

		/** The simulation type. */
		private int simulationType;

		/** The has simulation type. */
		private boolean hasSimulationType = false;

		/** The ecg data. */
		private EcgData ecgData;

		/** The has ecg data. */
		private boolean hasEcgData = false;


		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * Method setMessageType.
		 * @param messageType int
		 * @return Builder
		 */
		public Builder setMessageType(final int messageType) {
			this.messageType = messageType;
			this.hasMessageType = true;
			return this;
		}

		/**
		 * Method setTestType.
		 * @param testType int
		 * @return Builder
		 */
		public Builder setTestType(final int testType) {
			this.testType = testType;
			this.hasTestType = true;
			return this;
		}

		/**
		 * Method setSimulationType.
		 * @param simulationType int
		 * @return Builder
		 */
		public Builder setSimulationType(final int simulationType) {
			this.simulationType = simulationType;
			this.hasSimulationType = true;
			return this;
		}

		/**
		 * Method setEcgData.
		 * @param ecgData EcgData
		 * @return Builder
		 */
		public Builder setEcgData(final EcgData ecgData) {
			this.ecgData = ecgData;
			this.hasEcgData = true;
			return this;
		}

		/**
		 * Method build.
		 * @return Test
		 */
		public Test build() {
			return new Test(this);
		}
	}

	/**
	 * Method getMessageType.
	 * @return int
	 */
	public int getMessageType() {
		return messageType;
	}

	/**
	 * Method getTestType.
	 * @return int
	 */
	public int getTestType() {
		return testType;
	}

	/**
	 * Method hasTestType.
	 * @return boolean
	 */
	public boolean hasTestType() {
		return hasTestType;
	}

	/**
	 * Method getSimulationType.
	 * @return int
	 */
	public int getSimulationType() {
		return simulationType;
	}

	/**
	 * Method hasSimulationType.
	 * @return boolean
	 */
	public boolean hasSimulationType() {
		return hasSimulationType;
	}

	/**
	 * Method getEcgData.
	 * @return EcgData
	 */
	public EcgData getEcgData() {
		return ecgData;
	}

	/**
	 * Method hasEcgData.
	 * @return boolean
	 */
	public boolean hasEcgData() {
		return hasEcgData;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final String TAB = "   ";
		String retValue = "";
		retValue += this.getClass().getName() + "(";
		retValue += "messageType = " + this.messageType + TAB;
		if(hasTestType) retValue += "testType = " + this.testType + TAB;
		if(hasSimulationType) retValue += "simulationType = " + this.simulationType + TAB;
		if(hasEcgData) retValue += "ecgData = " + this.ecgData + TAB;
		retValue += ")";

		return retValue;
	}

	// Override
	/**
	 * Method computeSize.
	 * @return int
	 * @see net.jarlehansen.protobuf.javame.CustomListWriter#computeSize()
	 */
	public int computeSize() {
		int totalSize = 0;
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberMessageType, messageType);
		if(hasTestType) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberTestType, testType);
		if(hasSimulationType) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberSimulationType, simulationType);
		totalSize += computeNestedMessageSize();

		return totalSize;
	}

	/**
	 * Method computeNestedMessageSize.
	 * @return int
	 */
	private int computeNestedMessageSize() {
		int messageSize = 0;
		if(hasEcgData) messageSize += ComputeSizeUtil.computeMessageSize(fieldNumberEcgData, ecgData.computeSize());

		return messageSize;
	}

	// Override
	/**
	 * Method writeFields.
	 *
	 * @param writer OutputWriter
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see net.jarlehansen.protobuf.javame.CustomListWriter#writeFields(OutputWriter)
	 */
	public void writeFields(final OutputWriter writer) throws IOException {
		writer.writeInt(fieldNumberMessageType, messageType);
		if(hasTestType) writer.writeInt(fieldNumberTestType, testType);
		if(hasSimulationType) writer.writeInt(fieldNumberSimulationType, simulationType);
		if(hasEcgData) { writer.writeMessage(fieldNumberEcgData, ecgData.computeSize()); ecgData.writeFields(writer); }
	}

	/**
	 * Method parseFields.
	 *
	 * @param reader InputReader
	 * @return Test
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static Test parseFields(final InputReader reader) throws IOException {
		int nextFieldNumber = getNextFieldNumber(reader);
		final Builder builder = Test.newBuilder();

		while (nextFieldNumber > 0) {
			if(!populateBuilderWithField(reader, builder, nextFieldNumber)) {
				reader.getPreviousTagDataTypeAndReadContent();
			}
			nextFieldNumber = getNextFieldNumber(reader);
		}

		return builder.build();
	}

	/**
	 * Method getNextFieldNumber.
	 *
	 * @param reader InputReader
	 * @return int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static int getNextFieldNumber(final InputReader reader) throws IOException {
		return reader.getNextFieldNumber();
	}

	/**
	 * Method populateBuilderWithField.
	 *
	 * @param reader InputReader
	 * @param builder Builder
	 * @param fieldNumber int
	 * @return boolean
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static boolean populateBuilderWithField(final InputReader reader, final Builder builder, final int fieldNumber) throws IOException {
		boolean fieldFound = true;
		if (fieldNumber == fieldNumberMessageType) {
			builder.setMessageType(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberTestType) {
			builder.setTestType(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberSimulationType) {
			builder.setSimulationType(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberEcgData) {
			java.util.Vector vcEcgData = reader.readMessages(fieldNumberEcgData);
			for (int i = 0; i < vcEcgData.size(); i++) {
				byte[] eachBinData = (byte[]) vcEcgData.elementAt(i);
				EcgData.Builder builderEcgData = EcgData.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolEcgData = true;
				int nestedFieldEcgData = -1;
				while (boolEcgData) {
					nestedFieldEcgData = getNextFieldNumber(innerInputReader);
					boolEcgData = EcgData.populateBuilderWithField(innerInputReader, builderEcgData, nestedFieldEcgData);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.setEcgData(builderEcgData.build());
			}

		} else {
			fieldFound = false;
		}
		return fieldFound;
	}

	/**
	 * Method setUnknownTagHandler.
	 * @param unknownTagHandler UnknownTagHandler
	 */
	public static void setUnknownTagHandler(final UnknownTagHandler unknownTagHandler) {
		Test.unknownTagHandler = unknownTagHandler;
	}

	/**
	 * Method parseFrom.
	 *
	 * @param data byte[]
	 * @return Test
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Test parseFrom(final byte[] data) throws IOException {
		return parseFields(new InputReader(data, unknownTagHandler));
	}

	/**
	 * Method parseFrom.
	 *
	 * @param inputStream InputStream
	 * @return Test
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Test parseFrom(final InputStream inputStream) throws IOException {
		return parseFields(new InputReader(inputStream, unknownTagHandler));
	}

	/**
	 * Method parseDelimitedFrom.
	 *
	 * @param inputStream InputStream
	 * @return Test
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Test parseDelimitedFrom(final InputStream inputStream) throws IOException {
		final int limit = DelimitedSizeUtil.readDelimitedSize(inputStream);
		return parseFields(new InputReader(new DelimitedInputStream(inputStream, limit), unknownTagHandler));
	}
}