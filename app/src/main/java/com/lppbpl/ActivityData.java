/*
 *
 */
package com.lppbpl;
// Generated by proto2javame, Mon Jul 29 11:00:28 IST 2013.

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
 * The Class ActivityData.
 */
public final class ActivityData extends AbstractOutputWriter {

	/** The unknown tag handler. */
	private static UnknownTagHandler unknownTagHandler = DefaultUnknownTagHandlerImpl.newInstance();

	/** The total steps. */
	private final int totalSteps;

	/** The Constant fieldNumberTotalSteps. */
	private static final int fieldNumberTotalSteps = 1;

	/** The has total steps. */
	private final boolean hasTotalSteps;

	/** The total distance. */
	private final int totalDistance;

	/** The Constant fieldNumberTotalDistance. */
	private static final int fieldNumberTotalDistance = 2;

	/** The has total distance. */
	private final boolean hasTotalDistance;

	/** The total energy. */
	private final int totalEnergy;

	/** The Constant fieldNumberTotalEnergy. */
	private static final int fieldNumberTotalEnergy = 3;

	/** The has total energy. */
	private final boolean hasTotalEnergy;

	/** The time covered sofar. */
	private final int timeCoveredSofar;

	/** The Constant fieldNumberTimeCoveredSofar. */
	private static final int fieldNumberTimeCoveredSofar = 4;

	/** The has time covered sofar. */
	private final boolean hasTimeCoveredSofar;

	/** The annotation txt. */
	private final String annotationTxt;

	/** The Constant fieldNumberAnnotationTxt. */
	private static final int fieldNumberAnnotationTxt = 5;

	/** The has annotation txt. */
	private final boolean hasAnnotationTxt;

	/** The starting time. */
	private final long startingTime;

	/** The Constant fieldNumberStartingTime. */
	private static final int fieldNumberStartingTime = 6;

	/** The has starting time. */
	private final boolean hasStartingTime;


	/**
	 * Method newBuilder.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructor for ActivityData.
	 * @param builder Builder
	 */
	private ActivityData(final Builder builder) {
		if (true) {
			this.totalSteps = builder.totalSteps;
			this.hasTotalSteps = builder.hasTotalSteps;
			this.totalDistance = builder.totalDistance;
			this.hasTotalDistance = builder.hasTotalDistance;
			this.totalEnergy = builder.totalEnergy;
			this.hasTotalEnergy = builder.hasTotalEnergy;
			this.timeCoveredSofar = builder.timeCoveredSofar;
			this.hasTimeCoveredSofar = builder.hasTimeCoveredSofar;
			this.annotationTxt = builder.annotationTxt;
			this.hasAnnotationTxt = builder.hasAnnotationTxt;
			this.startingTime = builder.startingTime;
			this.hasStartingTime = builder.hasStartingTime;
		} else {
			throw new UninitializedMessageException("Not all required fields were included (false = not included in message), " +
				"");
		}
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The total steps. */
		private int totalSteps;

		/** The has total steps. */
		private boolean hasTotalSteps = false;

		/** The total distance. */
		private int totalDistance;

		/** The has total distance. */
		private boolean hasTotalDistance = false;

		/** The total energy. */
		private int totalEnergy;

		/** The has total energy. */
		private boolean hasTotalEnergy = false;

		/** The time covered sofar. */
		private int timeCoveredSofar;

		/** The has time covered sofar. */
		private boolean hasTimeCoveredSofar = false;

		/** The annotation txt. */
		private String annotationTxt;

		/** The has annotation txt. */
		private boolean hasAnnotationTxt = false;

		/** The starting time. */
		private long startingTime;

		/** The has starting time. */
		private boolean hasStartingTime = false;


		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * Method setTotalSteps.
		 * @param totalSteps int
		 * @return Builder
		 */
		public Builder setTotalSteps(final int totalSteps) {
			this.totalSteps = totalSteps;
			this.hasTotalSteps = true;
			return this;
		}

		/**
		 * Method setTotalDistance.
		 * @param totalDistance int
		 * @return Builder
		 */
		public Builder setTotalDistance(final int totalDistance) {
			this.totalDistance = totalDistance;
			this.hasTotalDistance = true;
			return this;
		}

		/**
		 * Method setTotalEnergy.
		 * @param totalEnergy int
		 * @return Builder
		 */
		public Builder setTotalEnergy(final int totalEnergy) {
			this.totalEnergy = totalEnergy;
			this.hasTotalEnergy = true;
			return this;
		}

		/**
		 * Method setTimeCoveredSofar.
		 * @param timeCoveredSofar int
		 * @return Builder
		 */
		public Builder setTimeCoveredSofar(final int timeCoveredSofar) {
			this.timeCoveredSofar = timeCoveredSofar;
			this.hasTimeCoveredSofar = true;
			return this;
		}

		/**
		 * Method setAnnotationTxt.
		 * @param annotationTxt String
		 * @return Builder
		 */
		public Builder setAnnotationTxt(final String annotationTxt) {
			this.annotationTxt = annotationTxt;
			this.hasAnnotationTxt = true;
			return this;
		}

		/**
		 * Method setStartingTime.
		 * @param startingTime long
		 * @return Builder
		 */
		public Builder setStartingTime(final long startingTime) {
			this.startingTime = startingTime;
			this.hasStartingTime = true;
			return this;
		}

		/**
		 * Method build.
		 * @return ActivityData
		 */
		public ActivityData build() {
			return new ActivityData(this);
		}
	}

	/**
	 * Method getTotalSteps.
	 * @return int
	 */
	public int getTotalSteps() {
		return totalSteps;
	}

	/**
	 * Method hasTotalSteps.
	 * @return boolean
	 */
	public boolean hasTotalSteps() {
		return hasTotalSteps;
	}

	/**
	 * Method getTotalDistance.
	 * @return int
	 */
	public int getTotalDistance() {
		return totalDistance;
	}

	/**
	 * Method hasTotalDistance.
	 * @return boolean
	 */
	public boolean hasTotalDistance() {
		return hasTotalDistance;
	}

	/**
	 * Method getTotalEnergy.
	 * @return int
	 */
	public int getTotalEnergy() {
		return totalEnergy;
	}

	/**
	 * Method hasTotalEnergy.
	 * @return boolean
	 */
	public boolean hasTotalEnergy() {
		return hasTotalEnergy;
	}

	/**
	 * Method getTimeCoveredSofar.
	 * @return int
	 */
	public int getTimeCoveredSofar() {
		return timeCoveredSofar;
	}

	/**
	 * Method hasTimeCoveredSofar.
	 * @return boolean
	 */
	public boolean hasTimeCoveredSofar() {
		return hasTimeCoveredSofar;
	}

	/**
	 * Method getAnnotationTxt.
	 * @return String
	 */
	public String getAnnotationTxt() {
		return annotationTxt;
	}

	/**
	 * Method hasAnnotationTxt.
	 * @return boolean
	 */
	public boolean hasAnnotationTxt() {
		return hasAnnotationTxt;
	}

	/**
	 * Method getStartingTime.
	 * @return long
	 */
	public long getStartingTime() {
		return startingTime;
	}

	/**
	 * Method hasStartingTime.
	 * @return boolean
	 */
	public boolean hasStartingTime() {
		return hasStartingTime;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final String TAB = "   ";
		String retValue = "";
		retValue += this.getClass().getName() + "(";
		if(hasTotalSteps) retValue += "totalSteps = " + this.totalSteps + TAB;
		if(hasTotalDistance) retValue += "totalDistance = " + this.totalDistance + TAB;
		if(hasTotalEnergy) retValue += "totalEnergy = " + this.totalEnergy + TAB;
		if(hasTimeCoveredSofar) retValue += "timeCoveredSofar = " + this.timeCoveredSofar + TAB;
		if(hasAnnotationTxt) retValue += "annotationTxt = " + this.annotationTxt + TAB;
		if(hasStartingTime) retValue += "startingTime = " + this.startingTime + TAB;
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
		if(hasTotalSteps) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberTotalSteps, totalSteps);
		if(hasTotalDistance) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberTotalDistance, totalDistance);
		if(hasTotalEnergy) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberTotalEnergy, totalEnergy);
		if(hasTimeCoveredSofar) totalSize += ComputeSizeUtil.computeIntSize(fieldNumberTimeCoveredSofar, timeCoveredSofar);
		if(hasAnnotationTxt) totalSize += ComputeSizeUtil.computeStringSize(fieldNumberAnnotationTxt, annotationTxt);
		if(hasStartingTime) totalSize += ComputeSizeUtil.computeLongSize(fieldNumberStartingTime, startingTime);
		totalSize += computeNestedMessageSize();

		return totalSize;
	}

	/**
	 * Method computeNestedMessageSize.
	 * @return int
	 */
	private int computeNestedMessageSize() {
		int messageSize = 0;

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
		if(hasTotalSteps) writer.writeInt(fieldNumberTotalSteps, totalSteps);
		if(hasTotalDistance) writer.writeInt(fieldNumberTotalDistance, totalDistance);
		if(hasTotalEnergy) writer.writeInt(fieldNumberTotalEnergy, totalEnergy);
		if(hasTimeCoveredSofar) writer.writeInt(fieldNumberTimeCoveredSofar, timeCoveredSofar);
		if(hasAnnotationTxt) writer.writeString(fieldNumberAnnotationTxt, annotationTxt);
		if(hasStartingTime) writer.writeLong(fieldNumberStartingTime, startingTime);
	}

	/**
	 * Method parseFields.
	 *
	 * @param reader InputReader
	 * @return ActivityData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static ActivityData parseFields(final InputReader reader) throws IOException {
		int nextFieldNumber = getNextFieldNumber(reader);
		final Builder builder = ActivityData.newBuilder();

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
		if (fieldNumber == fieldNumberTotalSteps) {
			builder.setTotalSteps(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberTotalDistance) {
			builder.setTotalDistance(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberTotalEnergy) {
			builder.setTotalEnergy(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberTimeCoveredSofar) {
			builder.setTimeCoveredSofar(reader.readInt(fieldNumber));

		} else if (fieldNumber == fieldNumberAnnotationTxt) {
			builder.setAnnotationTxt(reader.readString(fieldNumber));

		} else if (fieldNumber == fieldNumberStartingTime) {
			builder.setStartingTime(reader.readLong(fieldNumber));

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
		ActivityData.unknownTagHandler = unknownTagHandler;
	}

	/**
	 * Method parseFrom.
	 *
	 * @param data byte[]
	 * @return ActivityData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ActivityData parseFrom(final byte[] data) throws IOException {
		return parseFields(new InputReader(data, unknownTagHandler));
	}

	/**
	 * Method parseFrom.
	 *
	 * @param inputStream InputStream
	 * @return ActivityData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ActivityData parseFrom(final InputStream inputStream) throws IOException {
		return parseFields(new InputReader(inputStream, unknownTagHandler));
	}

	/**
	 * Method parseDelimitedFrom.
	 *
	 * @param inputStream InputStream
	 * @return ActivityData
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ActivityData parseDelimitedFrom(final InputStream inputStream) throws IOException {
		final int limit = DelimitedSizeUtil.readDelimitedSize(inputStream);
		return parseFields(new InputReader(new DelimitedInputStream(inputStream, limit), unknownTagHandler));
	}
}