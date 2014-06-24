package network.remote.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClientTest
{
	private volatile boolean run = true;
	private String host;
	private int port;

	private final EventLoopGroup workerGroup = new NioEventLoopGroup();

	public ClientTest()
	{
		this("localhost", ServerTest.PORT);
	}

	public ClientTest(String host, int port)
	{
		this.host = host;
		this.port = port;
	}

	public void run()
	{
		createBootstrap(new Bootstrap(), workerGroup);
	}

	public Bootstrap createBootstrap(Bootstrap bootstrap, EventLoopGroup eventLoop)
	{
		if (bootstrap != null)
		{
			bootstrap.group(eventLoop);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.handler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception
				{
					socketChannel.pipeline().addFirst(new ClientHandler());
					socketChannel.pipeline().addLast(new CloseHandler(ClientTest.this));
				}
			});
			bootstrap.remoteAddress(host, port);
			bootstrap.connect().addListener(new ConnectionListener(this));
		}
		return bootstrap;
	}

	void close()
	{
		run = false;
		workerGroup.shutdownGracefully();
	}

	public static void main(String[] args)
	{
		new ClientTest().run();
	}

	class ConnectionListener implements ChannelFutureListener
	{
		private ClientTest client;

		public ConnectionListener(ClientTest client)
		{
			this.client = client;
		}

		@Override
		public void operationComplete(ChannelFuture channelFuture) throws Exception
		{
			if (!channelFuture.isSuccess())
			{
				System.out.println("Reconnect");
				final EventLoop loop = channelFuture.channel().eventLoop();
				if (run)
				{
					loop.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							client.createBootstrap(new Bootstrap(), loop);
						}
					}, 1L, TimeUnit.SECONDS);
				}
			}
			else
			{
				System.out.println("Success");
			}
		}
	}
}

class CloseHandler extends SimpleChannelInboundHandler<Object>
{
	private ClientTest client;

	public CloseHandler(ClientTest client)
	{
		this.client = client;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception
	{
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				client.createBootstrap(new Bootstrap(), eventLoop);
			}
		}, 1L, TimeUnit.SECONDS);
		super.channelInactive(ctx);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg)
	{
		ctx.fireChannelRead(msg);
	}
}

class ClientHandler extends ChannelHandlerAdapter
{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		ByteBuf m = (ByteBuf) msg; // (1)
		try
		{
			long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
			System.out.println(new Date(currentTimeMillis));
			ctx.close();
		}
		finally
		{
			m.release();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
		ctx.close();
	}
}