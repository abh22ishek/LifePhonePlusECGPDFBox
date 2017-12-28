/*
 *
 */
package com.lppbpl;
// Generated by proto2javame, Fri Oct 19 14:17:12 IST 2012.

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
 * The Class ConfigMessage.
 */
public final class ConfigMessage extends AbstractOutputWriter {

	/** The unknown tag handler. */
	private static UnknownTagHandler unknownTagHandler = DefaultUnknownTagHandlerImpl.newInstance();

	/** The config type. */
	private final int configType;

	/** The Constant fieldNumberConfigType. */
	private static final int fieldNumberConfigType = 1;

	/** The measurement config. */
	private final MeasurementConfig measurementConfig;

	/** The Constant fieldNumberMeasurementConfig. */
	private static final int fieldNumberMeasurementConfig = 2;

	/** The has measurement config. */
	private final boolean hasMeasurementConfig;

	/** The bluetooth config. */
	private final BluetoothConfig bluetoothConfig;

	/** The Constant fieldNumberBluetoothConfig. */
	private static final int fieldNumberBluetoothConfig = 3;

	/** The has bluetooth config. */
	private final boolean hasBluetoothConfig;


	/**
	 * Method newBuilder.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructor for ConfigMessage.
	 * @param builder Builder
	 */
	private ConfigMessage(final Builder builder) {
		if (builder.hasConfigType ) {
			this.configType = builder.configType;
			this.measurementConfig = builder.measurementConfig;
			this.hasMeasurementConfig = builder.hasMeasurementConfig;
			this.bluetoothConfig = builder.bluetoothConfig;
			this.hasBluetoothConfig = builder.hasBluetoothConfig;
		} else {
			throw new UninitializedMessageException("Not all required fields were included (false = not included in message), " +
				" configType:" + builder.hasConfigType + "");
		}
	}

	/**
	 * The Class ConfigType.
	 */
	public static class ConfigType {

		/** The Constant Measurement. */
		public static final int Measurement = 1;

		/** The Constant Bluetooth. */
		public static final int Bluetooth = 2;

		/**
		 * Method getStringValue.
		 * @param value int
		 * @return String
		 */
		public static String getStringValue(int value) {
			String retValue;

			if (value == 1) {
				retValue = "Measurement";

			} else if (value == 2) {
				retValue = "Bluetooth";

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

		/** The config type. */
		private int configType;

		/** The has config type. */
		private boolean hasConfigType = false;

		/** The measurement config. */
		private MeasurementConfig measurementConfig;

		/** The has measurement config. */
		private boolean hasMeasurementConfig = false;

		/** The bluetooth config. */
		private BluetoothConfig bluetoothConfig;

		/** The has bluetooth config. */
		private boolean hasBluetoothConfig = false;


		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * Method setConfigType.
		 * @param configType int
		 * @return Builder
		 */
		public Builder setConfigType(final int configType) {
			this.configType = configType;
			this.hasConfigType = true;
			return this;
		}

		/**
		 * Method setMeasurementConfig.
		 * @param measurementConfig MeasurementConfig
		 * @return Builder
		 */
		public Builder setMeasurementConfig(final MeasurementConfig measurementConfig) {
			this.measurementConfig = measurementConfig;
			this.hasMeasurementConfig = true;
			return this;
		}

		/**
		 * Method setBluetoothConfig.
		 * @param bluetoothConfig BluetoothConfig
		 * @return Builder
		 */
		public Builder setBluetoothConfig(final BluetoothConfig bluetoothConfig) {
			this.bluetoothConfig = bluetoothConfig;
			this.hasBluetoothConfig = true;
			return this;
		}

		/**
		 * Method build.
		 * @return ConfigMessage
		 */
		public ConfigMessage build() {
			return new ConfigMessage(this);
		}
	}

	/**
	 * Method getConfigType.
	 * @return int
	 */
	public int getConfigType() {
		return configType;
	}

	/**
	 * Method getMeasurementConfig.
	 * @return MeasurementConfig
	 */
	public MeasurementConfig getMeasurementConfig() {
		return measurementConfig;
	}

	/**
	 * Method hasMeasurementConfig.
	 * @return boolean
	 */
	public boolean hasMeasurementConfig() {
		return hasMeasurementConfig;
	}

	/**
	 * Method getBluetoothConfig.
	 * @return BluetoothConfig
	 */
	public BluetoothConfig getBluetoothConfig() {
		return bluetoothConfig;
	}

	/**
	 * Method hasBluetoothConfig.
	 * @return boolean
	 */
	public boolean hasBluetoothConfig() {
		return hasBluetoothConfig;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final String TAB = "   ";
		String retValue = "";
		retValue += this.getClass().getName() + "(";
		retValue += "configType = " + this.configType + TAB;
		if(hasMeasurementConfig) retValue += "measurementConfig = " + this.measurementConfig + TAB;
		if(hasBluetoothConfig) retValue += "bluetoothConfig = " + this.bluetoothConfig + TAB;
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
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberConfigType, configType);
		totalSize += computeNestedMessageSize();

		return totalSize;
	}

	/**
	 * Method computeNestedMessageSize.
	 * @return int
	 */
	private int computeNestedMessageSize() {
		int messageSize = 0;
		if(hasMeasurementConfig) messageSize += ComputeSizeUtil.computeMessageSize(fieldNumberMeasurementConfig, measurementConfig.computeSize());
		if(hasBluetoothConfig) messageSize += ComputeSizeUtil.computeMessageSize(fieldNumberBluetoothConfig, bluetoothConfig.computeSize());

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
		writer.writeInt(fieldNumberConfigType, configType);
		if(hasMeasurementConfig) { writer.writeMessage(fieldNumberMeasurementConfig, measurementConfig.computeSize()); measurementConfig.writeFields(writer); }
		if(hasBluetoothConfig) { writer.writeMessage(fieldNumberBluetoothConfig, bluetoothConfig.computeSize()); bluetoothConfig.writeFields(writer); }
	}

	/**
	 * Method parseFields.
	 *
	 * @param reader InputReader
	 * @return ConfigMessage
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static ConfigMessage parseFields(final InputReader reader) throws IOException {
		int nextFieldNumber = getNextFieldNumber(reader);
		final Builder builder = ConfigMessage.newBuilder();

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
		if (fieldNumber == fieldNumberConfigType) {
			builder.setConfigType(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberMeasurementConfig) {
			java.util.Vector vcMeasurementConfig = reader.readMessages(fieldNumberMeasurementConfig);
			for (int i = 0; i < vcMeasurementConfig.size(); i++) {
				byte[] eachBinData = (byte[]) vcMeasurementConfig.elementAt(i);
				MeasurementConfig.Builder builderMeasurementConfig = MeasurementConfig.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolMeasurementConfig = true;
				int nestedFieldMeasurementConfig = -1;
				while (boolMeasurementConfig) {
					nestedFieldMeasurementConfig = getNextFieldNumber(innerInputReader);
					boolMeasurementConfig = MeasurementConfig.populateBuilderWithField(innerInputReader, builderMeasurementConfig, nestedFieldMeasurementConfig);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.setMeasurementConfig(builderMeasurementConfig.build());
			}

		} else if (fieldNumber == fieldNumberBluetoothConfig) {
			java.util.Vector vcBluetoothConfig = reader.readMessages(fieldNumberBluetoothConfig);
			for (int i = 0; i < vcBluetoothConfig.size(); i++) {
				byte[] eachBinData = (byte[]) vcBluetoothConfig.elementAt(i);
				BluetoothConfig.Builder builderBluetoothConfig = BluetoothConfig.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolBluetoothConfig = true;
				int nestedFieldBluetoothConfig = -1;
				while (boolBluetoothConfig) {
					nestedFieldBluetoothConfig = getNextFieldNumber(innerInputReader);
					boolBluetoothConfig = BluetoothConfig.populateBuilderWithField(innerInputReader, builderBluetoothConfig, nestedFieldBluetoothConfig);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.setBluetoothConfig(builderBluetoothConfig.build());
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
		ConfigMessage.unknownTagHandler = unknownTagHandler;
	}

	/**
	 * Method parseFrom.
	 *
	 * @param data byte[]
	 * @return ConfigMessage
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ConfigMessage parseFrom(final byte[] data) throws IOException {
		return parseFields(new InputReader(data, unknownTagHandler));
	}

	/**
	 * Method parseFrom.
	 *
	 * @param inputStream InputStream
	 * @return ConfigMessage
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ConfigMessage parseFrom(final InputStream inputStream) throws IOException {
		return parseFields(new InputReader(inputStream, unknownTagHandler));
	}

	/**
	 * Method parseDelimitedFrom.
	 *
	 * @param inputStream InputStream
	 * @return ConfigMessage
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ConfigMessage parseDelimitedFrom(final InputStream inputStream) throws IOException {
		final int limit = DelimitedSizeUtil.readDelimitedSize(inputStream);
		return parseFields(new InputReader(new DelimitedInputStream(inputStream, limit), unknownTagHandler));
	}
}