SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[applog](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[date] [datetime] NOT NULL,
	[host] [varchar](40) NULL,
	[application] [varchar](50) NULL,
	[category] [varchar](100) NULL,
	[thread] [varchar](50) NULL,
	[priority] [varchar](20) NULL,
	[message] [varchar](max) NULL,
	[throwable] [varchar](max) NULL,
 CONSTRAINT [PK_applog] PRIMARY KEY CLUSTERED 
(
	[id] DESC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF