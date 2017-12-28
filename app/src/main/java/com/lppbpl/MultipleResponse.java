/*
 *
 */
package com.lppbpl;
// Generated by proto2javame, Tue May 22 15:05:13 IST 2012.

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
 * The Class MultipleResponse.
 */
public final class MultipleResponse extends AbstractOutputWriter {

	/** The unknown tag handler. */
	private static UnknownTagHandler unknownTagHandler = DefaultUnknownTagHandlerImpl.newInstance();

	/** The multi resp type. */
	private final int multiRespType;

	/** The Constant fieldNumberMultiRespType. */
	private static final int fieldNumberMultiRespType = 1;

	/** The multi measurement type. */
	private final int multiMeasurementType;

	/** The Constant fieldNumberMultiMeasurementType. */
	private static final int fieldNumberMultiMeasurementType = 2;

	/** The no records. */
	private final int noRecords;

	/** The Constant fieldNumberNoRecords. */
	private static final int fieldNumberNoRecords = 3;

	/** The has no records. */
	private final boolean hasNoRecords;

	/** The timestamp. */
	private final java.util.Vector timestamp;

	/** The Constant fieldNumberTimestamp. */
	private static final int fieldNumberTimestamp = 4;

	/** The ecg list. */
	private final java.util.Vector ecgList;

	/** The Constant fieldNumberEcgList. */
	private static final int fieldNumberEcgList = 20;

	/** The ecg data. */
	private final EcgData ecgData;

	/** The Constant fieldNumberEcgData. */
	private static final int fieldNumberEcgData = 21;

	/** The has ecg data. */
	private final boolean hasEcgData;

	/** The hr data. */
	private final java.util.Vector hrData;

	/** The Constant fieldNumberHrData. */
	private static final int fieldNumberHrData = 22;

	/** The act data. */
	private final java.util.Vector actData;

	/** The Constant fieldNumberActData. */
	private static final int fieldNumberActData = 23;

	/** The temp data. */
	private final java.util.Vector tempData;

	/** The Constant fieldNumberTempData. */
	private static final int fieldNumberTempData = 24;

	/** The bg data. */
	private final java.util.Vector bgData;

	/** The Constant fieldNumberBgData. */
	private static final int fieldNumberBgData = 25;


	/**
	 * Method newBuilder.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructor for MultipleResponse.
	 * @param builder Builder
	 */
	private MultipleResponse(final Builder builder) {
		if (builder.hasMultiRespType && builder.hasMultiMeasurementType ) {
			this.multiRespType = builder.multiRespType;
			this.multiMeasurementType = builder.multiMeasurementType;
			this.noRecords = builder.noRecords;
			this.hasNoRecords = builder.hasNoRecords;
			this.timestamp = builder.timestamp;
			this.ecgList = builder.ecgList;
			this.ecgData = builder.ecgData;
			this.hasEcgData = builder.hasEcgData;
			this.hrData = builder.hrData;
			this.actData = builder.actData;
			this.tempData = builder.tempData;
			this.bgData = builder.bgData;
		} else {
			throw new UninitializedMessageException("Not all required fields were included (false = not included in message), " +
				" multiRespType:" + builder.hasMultiRespType + " multiMeasurementType:" + builder.hasMultiMeasurementType + "");
		}
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The multi resp type. */
		private int multiRespType;

		/** The has multi resp type. */
		private boolean hasMultiRespType = false;

		/** The multi measurement type. */
		private int multiMeasurementType;

		/** The has multi measurement type. */
		private boolean hasMultiMeasurementType = false;

		/** The no records. */
		private int noRecords;

		/** The has no records. */
		private boolean hasNoRecords = false;

		/** The timestamp. */
		private java.util.Vector timestamp = new java.util.Vector();

		/** The has timestamp. */
		private boolean hasTimestamp = false;

		/** The ecg list. */
		private java.util.Vector ecgList = new java.util.Vector();

		/** The has ecg list. */
		private boolean hasEcgList = false;

		/** The ecg data. */
		private EcgData ecgData;

		/** The has ecg data. */
		private boolean hasEcgData = false;

		/** The hr data. */
		private java.util.Vector hrData = new java.util.Vector();

		/** The has hr data. */
		private boolean hasHrData = false;

		/** The act data. */
		private java.util.Vector actData = new java.util.Vector();

		/** The has act data. */
		private boolean hasActData = false;

		/** The temp data. */
		private java.util.Vector tempData = new java.util.Vector();

		/** The has temp data. */
		private boolean hasTempData = false;

		/** The bg data. */
		private java.util.Vector bgData = new java.util.Vector();

		/** The has bg data. */
		private boolean hasBgData = false;


		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * Method setMultiRespType.
		 * @param multiRespType int
		 * @return Builder
		 */
		public Builder setMultiRespType(final int multiRespType) {
			this.multiRespType = multiRespType;
			this.hasMultiRespType = true;
			return this;
		}

		/**
		 * Method setMultiMeasurementType.
		 * @param multiMeasurementType int
		 * @return Builder
		 */
		public Builder setMultiMeasurementType(final int multiMeasurementType) {
			this.multiMeasurementType = multiMeasurementType;
			this.hasMultiMeasurementType = true;
			return this;
		}

		/**
		 * Method setNoRecords.
		 * @param noRecords int
		 * @return Builder
		 */
		public Builder setNoRecords(final int noRecords) {
			this.noRecords = noRecords;
			this.hasNoRecords = true;
			return this;
		}

		/**
		 * Method setTimestamp.
		 * @param timestamp java.util.Vector
		 * @return Builder
		 */
		public Builder setTimestamp(final java.util.Vector timestamp) {
			if(!hasTimestamp) {
				hasTimestamp = true;
			}
			this.timestamp = timestamp;
			return this;
		}


		/**
		 * Method addElementTimestamp.
		 * @param element long
		 * @return Builder
		 */
		public Builder addElementTimestamp(final long element) {
			if(!hasTimestamp) {
				hasTimestamp = true;
			}
			timestamp.addElement(new Long(element));
			return this;
		}

		/**
		 * Method setEcgList.
		 * @param ecgList java.util.Vector
		 * @return Builder
		 */
		public Builder setEcgList(final java.util.Vector ecgList) {
			if(!hasEcgList) {
				hasEcgList = true;
			}
			this.ecgList = ecgList;
			return this;
		}


		/**
		 * Method addElementEcgList.
		 * @param element EcgMeasurementList
		 * @return Builder
		 */
		public Builder addElementEcgList(final EcgMeasurementList element) {
			if(!hasEcgList) {
				hasEcgList = true;
			}
			ecgList.addElement(element);
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
		 * Method setHrData.
		 * @param hrData java.util.Vector
		 * @return Builder
		 */
		public Builder setHrData(final java.util.Vector hrData) {
			if(!hasHrData) {
				hasHrData = true;
			}
			this.hrData = hrData;
			return this;
		}


		/**
		 * Method addElementHrData.
		 * @param element HrData
		 * @return Builder
		 */
		public Builder addElementHrData(final HrData element) {
			if(!hasHrData) {
				hasHrData = true;
			}
			hrData.addElement(element);
			return this;
		}

		/**
		 * Method setActData.
		 * @param actData java.util.Vector
		 * @return Builder
		 */
		public Builder setActData(final java.util.Vector actData) {
			if(!hasActData) {
				hasActData = true;
			}
			this.actData = actData;
			return this;
		}


		/**
		 * Method addElementActData.
		 * @param element ActivityData
		 * @return Builder
		 */
		public Builder addElementActData(final ActivityData element) {
			if(!hasActData) {
				hasActData = true;
			}
			actData.addElement(element);
			return this;
		}

		/**
		 * Method setTempData.
		 * @param tempData java.util.Vector
		 * @return Builder
		 */
		public Builder setTempData(final java.util.Vector tempData) {
			if(!hasTempData) {
				hasTempData = true;
			}
			this.tempData = tempData;
			return this;
		}


		/**
		 * Method addElementTempData.
		 * @param element TemperatureData
		 * @return Builder
		 */
		public Builder addElementTempData(final TemperatureData element) {
			if(!hasTempData) {
				hasTempData = true;
			}
			tempData.addElement(element);
			return this;
		}

		/**
		 * Method setBgData.
		 * @param bgData java.util.Vector
		 * @return Builder
		 */
		public Builder setBgData(final java.util.Vector bgData) {
			if(!hasBgData) {
				hasBgData = true;
			}
			this.bgData = bgData;
			return this;
		}


		/**
		 * Method addElementBgData.
		 * @param element BgData
		 * @return Builder
		 */
		public Builder addElementBgData(final BgData element) {
			if(!hasBgData) {
				hasBgData = true;
			}
			bgData.addElement(element);
			return this;
		}

		/**
		 * Method build.
		 * @return MultipleResponse
		 */
		public MultipleResponse build() {
			return new MultipleResponse(this);
		}
	}

	/**
	 * Method getMultiRespType.
	 * @return int
	 */
	public int getMultiRespType() {
		return multiRespType;
	}

	/**
	 * Method getMultiMeasurementType.
	 * @return int
	 */
	public int getMultiMeasurementType() {
		return multiMeasurementType;
	}

	/**
	 * Method getNoRecords.
	 * @return int
	 */
	public int getNoRecords() {
		return noRecords;
	}

	/**
	 * Method hasNoRecords.
	 * @return boolean
	 */
	public boolean hasNoRecords() {
		return hasNoRecords;
	}

	/**
	 * Method getTimestamp.
	 * @return java.util.Vector
	 */
	public java.util.Vector getTimestamp() {
		return timestamp;
	}

	/**
	 * Method getEcgList.
	 * @return java.util.Vector
	 */
	public java.util.Vector getEcgList() {
		return ecgList;
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
	 * Method getHrData.
	 * @return java.util.Vector
	 */
	public java.util.Vector getHrData() {
		return hrData;
	}

	/**
	 * Method getActData.
	 * @return java.util.Vector
	 */
	public java.util.Vector getActData() {
		return actData;
	}

	/**
	 * Method getTempData.
	 * @return java.util.Vector
	 */
	public java.util.Vector getTempData() {
		return tempData;
	}

	/**
	 * Method getBgData.
	 * @return java.util.Vector
	 */
	public java.util.Vector getBgData() {
		return bgData;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final String TAB = "   ";
		String retValue = "";
		retValue += this.getClass().getName() + "(";
		retValue += "multiRespType = " + this.multiRespType + TAB;
		retValue += "multiMeasurementType = " + this.multiMeasurementType + TAB;
		if(hasNoRecords) retValue += "noRecords = " + this.noRecords + TAB;
		retValue += "timestamp = " + this.timestamp + TAB;
		retValue += "ecgList = " + this.ecgList + TAB;
		if(hasEcgData) retValue += "ecgData = " + this.ecgData + TAB;
		retValue += "hrData = " + this.hrData + TAB;
		retValue += "actData = " + this.actData + TAB;
		retValue += "tempData = " + this.tempData + TAB;
		retValue += "bgData = " + this.bgData + TAB;
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
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberMultiRespType, multiRespType);
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberMultiMeasurementType, multiMeasurementType);
		if(hasNoRecords) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberNoRecords, noRecords);
		totalSize += ComputeSizeUtil.computeListSize(fieldNumberTimestamp, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_LONG, timestamp);
		totalSize += computeNestedMessageSize();

		return totalSize;
	}

	/**
	 * Method computeNestedMessageSize.
	 * @return int
	 */
	private int computeNestedMessageSize() {
		int messageSize = 0;
		messageSize += ComputeSizeUtil.computeListSize(fieldNumberEcgList, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, ecgList);
		if(hasEcgData) messageSize += ComputeSizeUtil.computeMessageSize(fieldNumberEcgData, ecgData.computeSize());
		messageSize += ComputeSizeUtil.computeListSize(fieldNumberHrData, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, hrData);
		messageSize += ComputeSizeUtil.computeListSize(fieldNumberActData, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, actData);
		messageSize += ComputeSizeUtil.computeListSize(fieldNumberTempData, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, tempData);
		messageSize += ComputeSizeUtil.computeListSize(fieldNumberBgData, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, bgData);

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
		writer.writeInt(fieldNumberMultiRespType, multiRespType);
		writer.writeInt(fieldNumberMultiMeasurementType, multiMeasurementType);
		if(hasNoRecords) writer.writeInt(fieldNumberNoRecords, noRecords);
		writer.writeList(fieldNumberTimestamp, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_LONG, timestamp);
		writer.writeList(fieldNumberEcgList, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, ecgList);
		if(hasEcgData) { writer.writeMessage(fieldNumberEcgData, ecgData.computeSize()); ecgData.writeFields(writer); }
		writer.writeList(fieldNumberHrData, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, hrData);
		writer.writeList(fieldNumberActData, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, actData);
		writer.writeList(fieldNumberTempData, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, tempData);
		writer.writeList(fieldNumberBgData, net.jarlehansen.protobuf.javame.SupportedDataTypes.DATA_TYPE_CUSTOM, bgData);
	}

	/**
	 * Method parseFields.
	 *
	 * @param reader InputReader
	 * @return MultipleResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static MultipleResponse parseFields(final InputReader reader) throws IOException {
		int nextFieldNumber = getNextFieldNumber(reader);
		final Builder builder = MultipleResponse.newBuilder();

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
		if (fieldNumber == fieldNumberMultiRespType) {
			builder.setMultiRespType(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberMultiMeasurementType) {
			builder.setMultiMeasurementType(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberNoRecords) {
			builder.setNoRecords(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberTimestamp) {
			builder.addElementTimestamp(reader.readLong(fieldNumber));

		} else if (fieldNumber == fieldNumberEcgList) {
			java.util.Vector vcEcgList = reader.readMessages(fieldNumberEcgList);
			for (int i = 0; i < vcEcgList.size(); i++) {
				byte[] eachBinData = (byte[]) vcEcgList.elementAt(i);
				EcgMeasurementList.Builder builderEcgList = EcgMeasurementList.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolEcgList = true;
				int nestedFieldEcgList = -1;
				while (boolEcgList) {
					nestedFieldEcgList = getNextFieldNumber(innerInputReader);
					boolEcgList = EcgMeasurementList.populateBuilderWithField(innerInputReader, builderEcgList, nestedFieldEcgList);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.addElementEcgList(builderEcgList.build());
			}

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

		} else if (fieldNumber == fieldNumberHrData) {
			java.util.Vector vcHrData = reader.readMessages(fieldNumberHrData);
			for (int i = 0; i < vcHrData.size(); i++) {
				byte[] eachBinData = (byte[]) vcHrData.elementAt(i);
				HrData.Builder builderHrData = HrData.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolHrData = true;
				int nestedFieldHrData = -1;
				while (boolHrData) {
					nestedFieldHrData = getNextFieldNumber(innerInputReader);
					boolHrData = HrData.populateBuilderWithField(innerInputReader, builderHrData, nestedFieldHrData);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.addElementHrData(builderHrData.build());
			}

		} else if (fieldNumber == fieldNumberActData) {
			java.util.Vector vcActData = reader.readMessages(fieldNumberActData);
			for (int i = 0; i < vcActData.size(); i++) {
				byte[] eachBinData = (byte[]) vcActData.elementAt(i);
				ActivityData.Builder builderActData = ActivityData.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolActData = true;
				int nestedFieldActData = -1;
				while (boolActData) {
					nestedFieldActData = getNextFieldNumber(innerInputReader);
					boolActData = ActivityData.populateBuilderWithField(innerInputReader, builderActData, nestedFieldActData);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.addElementActData(builderActData.build());
			}

		} else if (fieldNumber == fieldNumberTempData) {
			java.util.Vector vcTempData = reader.readMessages(fieldNumberTempData);
			for (int i = 0; i < vcTempData.size(); i++) {
				byte[] eachBinData = (byte[]) vcTempData.elementAt(i);
				TemperatureData.Builder builderTempData = TemperatureData.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolTempData = true;
				int nestedFieldTempData = -1;
				while (boolTempData) {
					nestedFieldTempData = getNextFieldNumber(innerInputReader);
					boolTempData = TemperatureData.populateBuilderWithField(innerInputReader, builderTempData, nestedFieldTempData);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.addElementTempData(builderTempData.build());
			}

		} else if (fieldNumber == fieldNumberBgData) {
			java.util.Vector vcBgData = reader.readMessages(fieldNumberBgData);
			for (int i = 0; i < vcBgData.size(); i++) {
				byte[] eachBinData = (byte[]) vcBgData.elementAt(i);
				BgData.Builder builderBgData = BgData.newBuilder();
				InputReader innerInputReader = new InputReader(eachBinData, unknownTagHandler);
				boolean boolBgData = true;
				int nestedFieldBgData = -1;
				while (boolBgData) {
					nestedFieldBgData = getNextFieldNumber(innerInputReader);
					boolBgData = BgData.populateBuilderWithField(innerInputReader, builderBgData, nestedFieldBgData);
				}
				eachBinData = null;
				innerInputReader = null;
				builder.addElementBgData(builderBgData.build());
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
		MultipleResponse.unknownTagHandler = unknownTagHandler;
	}

	/**
	 * Method parseFrom.
	 *
	 * @param data byte[]
	 * @return MultipleResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static MultipleResponse parseFrom(final byte[] data) throws IOException {
		return parseFields(new InputReader(data, unknownTagHandler));
	}

	/**
	 * Method parseFrom.
	 *
	 * @param inputStream InputStream
	 * @return MultipleResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static MultipleResponse parseFrom(final InputStream inputStream) throws IOException {
		return parseFields(new InputReader(inputStream, unknownTagHandler));
	}

	/**
	 * Method parseDelimitedFrom.
	 *
	 * @param inputStream InputStream
	 * @return MultipleResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static MultipleResponse parseDelimitedFrom(final InputStream inputStream) throws IOException {
		final int limit = DelimitedSizeUtil.readDelimitedSize(inputStream);
		return parseFields(new InputReader(new DelimitedInputStream(inputStream, limit), unknownTagHandler));
	}
}