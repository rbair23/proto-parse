package protoparse;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.protoparse.MalformedProtobufException;
import com.hedera.hashgraph.sdk.proto.Timestamp;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 5, time = 10)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ParserBench {
	private final byte[] protobuf = Timestamp.newBuilder().setNanos(1234).setSeconds(5678L).build().toByteArray();

	@Benchmark
	public void parseTimestamp(Blackhole blackhole) throws MalformedProtobufException {
		blackhole.consume(TimestampParser.parse(protobuf));
	}

	@Benchmark
	public void parseTimestamp2(Blackhole blackhole) throws InvalidProtocolBufferException {
		blackhole.consume(Timestamp.parseFrom(protobuf));
	}

	/*
	RESULT :
	1. Using TransferList 102379.684 ops/s
	2. using long[] array in SideEffectsTracker 505066 ops.s
	*/
}
